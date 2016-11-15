/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
function countTest(){
    var req = new XMLHttpRequest();
    req.onreadystatechange = getReadyStateHandler(req,updateC);
    
    req.open("GET","HerokuMobileTest",true);
    req.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
    req.send("action=add");
}

function updateC(output) {
    
}

