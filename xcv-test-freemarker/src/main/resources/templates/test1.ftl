<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Hello World!</title>
</head>
<body>
Hello ${name}!<br>
遍历list中的数据:<br>
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>金额</td>
        <td>出生日期(年月日)</td>
        <td>出生日期(时分秒)</td>
        <td>出生日期(只时分秒)</td>
        <td>出生日期(自定义)</td>
    </tr>
    <#--?? 表示非空判断-->
    <#if stus??>
        <#list stus as stu>
            <tr>
                <td>${stu_index + 1}</td>
                <td  <#if stu.name == '小明'>style="background-color: aqua"</#if> >${stu.name}</td>
                <td>${stu.age}</td><#--结觉大于号小于号根标签冲突问题可以使用lt,gt符号代替比较符号或者是有括号吧表达式包围避免冲突-->
                <td <#if stu.money gt 300>style="background-color: #bbffaa" </#if>>${stu.money}</td>
                <td>${stu.birthday?date}</td>
                <td>${stu.birthday?datetime}</td>
                <td>${stu.birthday?time}</td>
                <td>${stu.birthday?string("yyyy年MM月dd日")}</td>
            </tr>
        </#list>
    </#if>
</table><br>
集合长度为: ${stus?size}<br>
map取值方式案例:><br>
1.map中括号方式取值:<br>
姓名:${(stuMap['stu1'].name)!''}<br>
年龄:${(stuMap['stu1'].age)!''}<br>
2.json方式取值:<br>
姓名:${(stuMap.stu1.name)!''}<br>
年龄:${(stuMap.stu1.age)!''}<br>
map遍历: <br>
<#list stuMap?keys as k>
姓名:${stuMap[k].name}<br>
年龄:${stuMap[k].age}<br>
</#list>
point : ${point?c}<br>
<#assign text="{'bank':'工商银行','account':'10101920201920212'}" />
<#assign data=text?eval />
开户行：${data.bank} <br>
账号：${data.account}
</body>
</html