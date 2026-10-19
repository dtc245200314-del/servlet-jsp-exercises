import http.cookiejar, urllib.request, urllib.parse, urllib.error, re, json, uuid, pathlib
base='http://127.0.0.1:18091'
op=urllib.request.build_opener(urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()))
checks=[]
def check(name, condition):
    checks.append({'name':name,'passed':bool(condition)})
    if not condition: raise AssertionError(name)
def req(path,data=None,status=200):
    try:
        r=op.open(base+path, None if data is None else urllib.parse.urlencode(data,doseq=True).encode(), timeout=20)
        code=r.status; text=r.read().decode(); final=r.url
    except urllib.error.HTTPError as e:
        code=e.code;text=e.read().decode(); final=e.url
    check(f'{path.split("?")[0]} HTTP {status}',code==status)
    return text
def token(html):return re.search(r'name="csrf" value="([^"]+)"',html).group(1)
marker='QA-'+uuid.uuid4().hex[:8]
for kind, fields, invalid in [
 ('customers',{'name':marker+' <script>alert(1)</script>','email':marker+'@example.com','address':'Hà Nội'}, {'email':'invalid'}),
 ('products',{'name':marker+' <script>alert(1)</script>','price':'123.45','description':'Mô tả thử nghiệm','manufacturer':'QA Factory'}, {'price':'-1'})]:
    route='/mvc-management/'+kind
    page=req(route+'?action=new'); csrf=token(page)
    req(route,{'action':'create',**fields},403)
    req(route,{'action':'create','csrf':csrf,**fields,**invalid},400)
    html=req(route,{'action':'create','csrf':csrf,**fields})
    check(kind+' HTML escaped', '&lt;script&gt;' in html and '<script>alert(1)</script>' not in html)
    row=re.search(r'<tr>.*?'+re.escape(marker)+r'.*?</tr>',html,re.S)
    # Last matching view link before marker's edit link belongs to the created row.
    rows=re.findall(r'<tr>.*?</tr>',html,re.S)
    own=next(x for x in rows if marker in x)
    id_=re.search(r'id=(\d+)',own).group(1)
    req(route+'?action=view&id='+id_)
    req(route+'?action=edit&id='+id_)
    html=req(route,{'action':'update','csrf':csrf,'id':id_,**fields,'name':marker+' updated'})
    check(kind+' update visible',marker+' updated' in html)
    html=req(route+'?q='+marker)
    check(kind+' search finds update',marker+' updated' in html)
    req(route+'?action=delete&id='+id_)
    html=req(route,{'action':'delete','csrf':csrf,'id':id_})
    check(kind+' delete removed',marker+' updated' not in html)
    req(route+'?action=view&id='+id_,status=404)
for mode in ['sql','sp']:
    route='/users/users'; query='?mode='+mode
    csrf=token(req(route+query))
    fields={'name':marker+' <b>test</b>','email':marker+mode+'@example.com','country':'QA Country'}
    req(route,{'action':'create','mode':mode,**fields},403)
    req(route,{'action':'create','mode':mode,'csrf':csrf,**fields,'email':'invalid'},400)
    html=req(route,{'action':'create','mode':mode,'csrf':csrf,**fields})
    check(mode+' escaped user',marker+' &lt;b&gt;test&lt;/b&gt;' in html)
    row=next(x for x in re.findall(r'<tr>.*?</tr>',html,re.S) if marker in x)
    id_=re.search(r'id=(\d+)',row).group(1)
    req(route+query+'&action=view&id='+id_)
    req(route+query+'&action=edit&id='+id_)
    html=req(route,{'action':'update','mode':mode,'csrf':csrf,'id':id_,**fields,'name':marker+' updated'})
    check(mode+' user update',marker+' updated' in html)
    html=req(route+query+'&country=QA+Country&sort=name')
    check(mode+' country filter',marker+' updated' in html and 'an@example.com' not in html)
    req(route+query+'&action=delete&id='+id_)
    html=req(route,{'action':'delete','mode':mode,'csrf':csrf,'id':id_})
    check(mode+' user deleted',marker+' updated' not in html)
    req(route+query+'&action=view&id='+id_,status=404)
csrf=token(req('/users/users'))
for action,parts in [
 ('without-failure',['<dd>false</dd>','<dd>true</dd>','<dd>2</dd>','10.00']),
 ('with-failure',['<dd>true</dd>','<dd>0</dd>','Không có hàng']),
 ('with-success',['<dd>false</dd>','<dd>2</dd>','999.99']),
 ('user-rollback',['SQL lỗi FK: true; số user còn lại sau rollback: 0'])]:
    html=req('/users/users',{'action':action,'csrf':csrf})
    check(action+' actual DB result',all(p in html for p in parts))
path=pathlib.Path(__file__).with_name('http-results.json')
path.write_text(json.dumps({'checks':checks,'passed':sum(x['passed'] for x in checks)},ensure_ascii=False,indent=2),encoding='utf8')
print(f'{len(checks)}/{len(checks)} HTTP checks passed; result saved.')
