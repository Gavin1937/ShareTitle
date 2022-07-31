
function doLogin()
{
    // calculate auth_hash & rm password
    var form = document.querySelector('[id="login"]')
    var username = form.querySelector('[id="username"]');
    var password = form.querySelector('[id="password"]');
    var auth_hash = CryptoJS.MD5(
        username.value.toLowerCase()+password.value
    ).toString().toLowerCase();
    
    form.querySelector('[id="auth_hash"]').value=auth_hash;
    form.removeChild(password);
    
    // post login request
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/login"); 
    
    xhr.send(form);
}

function forceLower(strInput) 
{
    strInput.value = strInput.value.toLowerCase();
}
