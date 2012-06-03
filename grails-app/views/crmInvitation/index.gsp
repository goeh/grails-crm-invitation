<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title><g:message code="crmInvitation.index.title" args="[user]"/></title>
</head>

<body>

<div class="row-fluid">
    <div class="span9">

        <header class="page-header">
            <h1><g:message code="crmInvitation.index.title" default="Invitations"
                           args="[user, invitations.size()]"/></h1>
        </header>

        <g:if test="${invitations}">
            <g:each in="${invitations}" var="invitation" status="i">
                <g:set var="reference" value="${invitation.reference}"/>
                <div class="well">
                    <g:form>
                        <input type="hidden" name="id" value="${invitation.id}"/>

                        <h2>${invitation.sender.encodeAsHTML()}
                            <small><g:formatDate type="date" date="${invitation.dateCreated}"/></small>
                        </h2>
                        <h4>${reference.encodeAsHTML()}</h4>
                        <tt:html name="crmInvitation.index.main">
                            <p>
                                ${invitation.sender.encodeAsHTML()} bjuder in dig till Avtala.se.
                                Genom att accepera denna inbjudan får du inte bara tillgång till en bra tjänst för dina egna avtal.
                                Du får dessutom behörighet att ta del av avtalen som ${invitation.sender.encodeAsHTML()} registrerat.
                            </p>
                        </tt:html>
                        <button name="_action_accept" class="btn btn-primary">Accept</button>
                        <button name="_action_deny" class="btn btn-danger">Deny</button>
                    </g:form>
                </div>
            </g:each>
        </g:if>
        <g:else>
            <h2><g:message code="crmInvitation.no.invitations.message" default="No invitations for {0}"
                           args="[user]"/></h2>
        </g:else>

    </div>

    <div class="span3">
        <tt:html name="crmInvitation.index.right"></tt:html>
    </div>
</div>

</body>
</html>
