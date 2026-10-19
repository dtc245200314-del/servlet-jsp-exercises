param([string]$BaseUrl='http://127.0.0.1:18088/practice')
$ErrorActionPreference='Stop'
$passed=0
function Assert-Http([string]$Path,[string]$Method,[hashtable]$Body,[int]$Status,[string]$Contains){
    $params=@{Uri=($BaseUrl+$Path);Method=$Method;SkipHttpErrorCheck=$true}
    if($null -ne $Body){$params.Body=$Body;$params.ContentType='application/x-www-form-urlencoded; charset=utf-8'}
    $response=Invoke-WebRequest @params
    if([int]$response.StatusCode -ne $Status){throw "$Method $Path status $($response.StatusCode), expected $Status"}
    if(-not $response.Content.Contains($Contains)){throw "$Method $Path missing expected content: $Contains"}
    $script:passed++
    Write-Output "PASS $Method $Path ($Status)"
}
Assert-Http '/' GET $null 200 'Bài tập Servlet và JSP'
Assert-Http '/hello' GET $null 200 'server-time'
Assert-Http '/login' POST @{username='admin';password='admin'} 200 'Welcome admin to website'
Assert-Http '/login' POST @{username='admin';password='wrong'} 401 'Login Error'
Assert-Http '/convert' POST @{rate='25000';usd='10.50'} 200 '262500.00 VNĐ'
Assert-Http '/convert' POST @{rate='-1';usd='10'} 400 'Giá trị không được âm'
Assert-Http '/convert' POST @{rate='NaN';usd='10'} 400 'Vui lòng nhập số hợp lệ'
Assert-Http '/translate' POST @{word=' HELLO '} 200 'Xin chào'
Assert-Http '/translate' POST @{word='unknown'} 200 'Không tìm thấy từ'
Assert-Http '/translate' POST @{word='<script>alert(1)</script>'} 200 '&lt;script&gt;alert(1)&lt;/script&gt;'
Assert-Http '/display-discount' POST @{description='Book';price='200';percent='10'} 200 '180.00'
Assert-Http '/display-discount' POST @{description='<b>Book</b>';price='200';percent='10'} 200 '&lt;b&gt;Book&lt;/b&gt;'
Assert-Http '/display-discount' POST @{description='Book';price='200';percent='101'} 400 'phần trăm phải từ 0 đến 100'
Assert-Http '/jsp-currency.jsp' GET $null 200 'converter.jsp'
Assert-Http '/converter.jsp' POST @{rate='25000';usd='2'} 200 '50000 VNĐ'
Assert-Http '/converter.jsp' POST @{rate='abc';usd='2'} 400 'Vui lòng nhập số hợp lệ'
Assert-Http '/jsp-dictionary.jsp' GET $null 200 'dictionary.jsp'
Assert-Http '/dictionary.jsp' POST @{search='book'} 200 'Quyển sách'
Assert-Http '/dictionary.jsp' POST @{search='<script>x</script>'} 200 '&lt;script&gt;x&lt;/script&gt;'
Assert-Http '/customers' GET $null 200 'Nguyễn Minh An'
Assert-Http '/calculator' GET $null 200 'Toán hạng thứ nhất'
Assert-Http '/calculator' POST @{left='7';right='2';operator='/'} 200 'Kết quả: 3.5'
Assert-Http '/calculator' POST @{left='7';right='0';operator='/'} 400 'Không thể chia cho 0'
Assert-Http '/calculator' POST @{left='7';right='2';operator='?'} 400 'Phép toán không hợp lệ'
Write-Output "All $passed HTTP/JSP checks passed."
