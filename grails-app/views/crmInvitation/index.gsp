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

        <div class="tabbable">

            <ul class="nav nav-tabs">
                <li class="active"><a href="#received" data-toggle="tab"><g:message
                        code="crmInvitation.tab.received.label"/>
                    <crm:countIndicator count="${invitations.size()}"/></a></li>
                <g:if test="${invited}">
                    <li><a href="#sent" data-toggle="tab"><g:message code="crmInvitation.tab.sent.label"/>
                        <crm:countIndicator count="${invited.size()}"/></a></li>
                </g:if>
                <crm:pluginViews location="tabs" var="view">
                    <li>
                        <a href="#${view.id}" data-toggle="tab">
                            ${message(code:view.label, default:view.label)}
                        </a>
                    </li>
                </crm:pluginViews>
            </ul>

            <div class="tab-content">

                <div class="tab-pane active" id="received">
                    <g:if test="${invitations}">
                        <g:each in="${invitations}" var="invitation" status="i">
                            <g:set var="reference" value="${invitation.reference}"/>
                            <div class="well">
                                <g:form>
                                    <input type="hidden" name="id" value="${invitation.id}"/>

                                    <h3>${invitation.sender.encodeAsHTML()}
                                        <small><g:formatDate type="date" date="${invitation.dateCreated}"/></small>
                                    </h3>
                                    <h4>${reference.encodeAsHTML()}</h4>
                                    <tt:html name="crmInvitation.index.main">
                                        <p>
                                            ${invitation.sender.encodeAsHTML()} bjuder in dig till <g:message
                                                    code="app.name" default="denna tjänst"/>.
                                            Genom att acceptera denna inbjudan får du inte bara tillgång till en bra tjänst för ditt eget bruk.
                                            Du får dessutom behörighet att ta del av &quot;${reference.encodeAsHTML()}&quot; som hanteras av ${invitation.sender.encodeAsHTML()}.
                                        </p>
                                    </tt:html>
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
                                       args="[user]"/></h3>
                    </g:else>
                </div>

                <g:if test="${invited}">
                    <div class="tab-pane" id="sent">
                        <table class="table table-striped">
                            <thead>
                            <tr>
                                <th><g:message code="crmInvitation.receiver.label" default="To"/></th>
                                <th><g:message code="crmInvitation.ref.label" default="Target"/></th>
                                <th><g:message code="crmInvitation.param.label" default="Parameter"/></th>
                                <th><g:message code="crmInvitation.dateCreated.label" default="Created"/></th>
                                <th><g:message code="crmInvitation.status.label" default="Status"/></th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${invited}" var="inv">
                                <tr>
                                    <td>${inv.receiver?.encodeAsHTML()}</td>
                                    <td>${inv.reference?.encodeAsHTML()}</td>
                                    <td>${inv.param?.encodeAsHTML()}</td>
                                    <td><g:formatDate format="yyyy-MM-dd" date="${inv.dateCreated}"/></td>
                                    <td>${message(code: 'crmInvitation.status.' + inv.status + '.label')}</td>
                                    <td>
                                        <crm:button type="link" action="cancel" id="${inv.id}"
                                                    visual="danger" class="btn-mini" icon="icon-trash icon-white"
                                                    label="crmInvitation.button.cancel.label"
                                                    confirm="crmInvitation.button.cancel.confirm.message"
                                                    args="${[inv.receiver]}"
                                                    permission="crmInvitation:cancel"/>
                                    </td>
                                </tr>
                            </g:each>
                            </tbody>
                        </table>
                    </div>
                </g:if>

                <crm:pluginViews location="tabs" var="view">
                    <div class="tab-pane" id="${view.id}">
                        <g:render template="${view.template}" model="${view.model}" plugin="${view.plugin}"/>
                    </div>
                </crm:pluginViews>

            </div>
        </div>
    </div>

    <div class="span3">
        <crm:submenu/>
        <tt:html name="crmInvitation.index.right"></tt:html>
    </div>
</div>

</body>
</html>
