param([string]$BaseUrl='http://127.0.0.1:18089/final-exam/products')
$ErrorActionPreference='Stop'
$session=[Microsoft.PowerShell.Commands.WebRequestSession]::new()
$passed=0
function Request([string]$Query,[string]$Method,[hashtable]$Body){
    $parameters=@{Uri=($BaseUrl+$Query);Method=$Method;WebSession=$session;SkipHttpErrorCheck=$true}
    if($null -ne $Body){$parameters.Body=$Body;$parameters.ContentType='application/x-www-form-urlencoded; charset=utf-8'}
    Invoke-WebRequest @parameters
}
function Check($Response,[int]$Status,[string]$Content){
    if([int]$Response.StatusCode -ne $Status){throw "Status $($Response.StatusCode), expected $Status"}
    if(-not $Response.Content.Contains($Content)){throw "Missing expected content: $Content"}
    $script:passed++
    Write-Output "PASS $passed ($Status): $Content"
}
$list=Request '' GET $null
Check $list 200 'Danh sách sản phẩm'
Check $list 200 'Bàn phím cơ'
$form=Request '?action=new' GET $null
Check $form 200 'Thêm sản phẩm'
$token=[regex]::Match($form.Content,'name="csrf" value="([^"]+)"').Groups[1].Value
if(-not $token){throw 'Missing CSRF token'}
Check (Request '' POST @{action='create';name='CSRF test';price='1';quantity='1'}) 403 'Phiên biểu mẫu không hợp lệ'
$suffix=[guid]::NewGuid().ToString('N').Substring(0,8)
$name='HTTP-QA-'+$suffix
$create=Request '' POST @{csrf=$token;action='create';name=$name;price='123.45';quantity='7'}
Check $create 200 'Đã thêm sản phẩm'
Check $create 200 $name
$id=[regex]::Match($create.Content,('<tr><td class="muted">#(\d+)</td><td class="product-name">'+[regex]::Escape($name))).Groups[1].Value
if(-not $id){throw 'Created product ID not found'}
$edit=Request ("?action=edit&id=$id") GET $null
Check $edit 200 ('value="'+$name+'"')
Check $edit 200 'value="123.45"'
Check $edit 200 'value="7"'
$updated='Sản phẩm đã sửa '+$suffix
Check (Request '' POST @{csrf=$token;action='update';id=$id;name=$updated;price='456.78';quantity='0'}) 200 'Đã cập nhật sản phẩm'
Check (Request '' GET $null) 200 $updated
Check (Request ("?action=delete&id=$id") GET $null) 200 'Xác nhận xóa'
Check (Request ("?action=edit&id=$id") GET $null) 200 $updated
Check (Request '' POST @{action='delete';id=$id}) 403 'Phiên biểu mẫu không hợp lệ'
Check (Request ("?action=edit&id=$id") GET $null) 200 $updated
Check (Request '' POST @{csrf=$token;action='delete';id=$id}) 200 'Đã xóa sản phẩm'
Check (Request ("?action=edit&id=$id") GET $null) 404 'Không tìm thấy sản phẩm'
Check (Request '?action=edit&id=bad' GET $null) 400 'ID sản phẩm không hợp lệ'
Check (Request '?action=unknown' GET $null) 400 'Chức năng không hợp lệ'
Check (Request '' POST @{csrf=$token;action='create';name=' ';price='1';quantity='1'}) 400 'Tên sản phẩm phải có'
Check (Request '' POST @{csrf=$token;action='create';name='Bad';price='-1';quantity='1'}) 400 'Giá phải không âm'
Check (Request '' POST @{csrf=$token;action='create';name='Bad';price='1.999';quantity='1'}) 400 '2 chữ số thập phân'
Check (Request '' POST @{csrf=$token;action='create';name='Bad';price='1';quantity='1.5'}) 400 'Số lượng phải là số nguyên'
$unsafe='<script>alert("'+$suffix+'")</script>'
$unsafePage=Request '' POST @{csrf=$token;action='create';name=$unsafe;price='1';quantity='1'}
Check $unsafePage 200 '&lt;script&gt;alert('
if($unsafePage.Content.Contains($unsafe)){throw 'Unescaped product name in HTML'}
$unsafeId=[regex]::Match($unsafePage.Content,('<tr><td class="muted">#(\d+)</td><td class="product-name">&lt;script&gt;alert\(&#034;'+$suffix)).Groups[1].Value
if(-not $unsafeId){$unsafeId=[regex]::Match($unsafePage.Content,('<tr><td class="muted">#(\d+)</td><td class="product-name">&lt;script&gt;alert\(&quot;'+$suffix)).Groups[1].Value}
if(-not $unsafeId){throw 'Cannot locate escaped QA product for cleanup'}
Check (Request '' POST @{csrf=$token;action='delete';id=$unsafeId}) 200 'Đã xóa sản phẩm'
Write-Output "All $passed final-exam HTTP/JSP checks passed."
