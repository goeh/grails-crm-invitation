<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title><g:message code="crmInvitation.index.title" args="[user.name]"/></title>
</head>

<body>

<div class="row-fluid">
    <div class="span9">

        <header class="page-header">
            <h1><g:message code="crmInvitation.index.title" default="Invitations"
                           args="[user.name, invitations.size()]"/></h1>
        </header>

        <g:if test="${invitations}">
            <g:each in="${invitations}" var="invitation" status="i">
                <g:set var="reference" value="${invitation.reference}"/>
                <div class="well">
                    <g:form>
                        <input type="hidden" name="id" value="${invitation.id}"/>

                        <tmpl:content invitation="${invitation}" reference="${reference}"/>

                        <button type="submit" name="_action_accept" class="btn btn-primary"><i
                                class="icon-ok icon-white"></i>
                            <g:message code="crmInvitation.button.accept.label" default="Accept"/></button>
                        <button type="submit" name="_action_deny" class="btn btn-danger"><i
                                class="icon-remove icon-white"></i>
                            <g:message code="crmInvitation.button.deny.label" default="Deny"/></button>
                    </g:form>
                </div>
            </g:each>
        </g:if>
        <g:else>
            <h3><g:message code="crmInvitation.no.invitations.message" default="No invitations for {0}"
                           args="[user.name]"/></h3>
        </g:else>
    </div>

    <div class="span3">
        <crm:submenu/>
    </div>
</div>

</body>
</html>
