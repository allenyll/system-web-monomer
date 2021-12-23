$(function(){
	swVerCode();//初始化生成随机数
});


//生成随机数
function swVerCode(len){
    len = len || 4;
    let $chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678';//默认去掉了容易混淆的字符oOLl,9gq,Vv,Uu,I1
    let maxPos = $chars.length;
    let code = '';
    for (let i = 0; i < len; i++) {
        code += $chars.charAt(Math.floor(Math.random() * maxPos));
    }
    $(".swVerCode").html(code);
}
