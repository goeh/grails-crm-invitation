<div id="modal-share" class="modal hide">
    <div class="modal-header">
        <a href="#" class="close" data-dismiss="modal">&times;</a>

        <h3><g:message code="crmInvitation.share.title" args="${[bean.toString()]}"/></h3>
    </div>

    <div class="well">
        <g:form action="share">
            <input type="hidden" name="id" value="${bean.id}"/>
            <input type="hidden" name="referer" value="${request.forwardURI - request.contextPath}"/>

            <div class="modal-body">
                <p><g:message code="crmInvitation.share.subtitle" args="${[bean.toString()]}"/></p>
            </div>

            <div class="control-group">
                <label class="control-label"><g:message code="crmInvitation.share.email.label" default="Email"/></label>

                <div class="controls">
                    <input type="email" name="email" class="span4"
                           placeholder="${message(code: 'crmInvitation.share.email.help', default: '')}"/>
                </div>
            </div>

            <div class="control-group">
                <label class="control-label"><g:message code="crmInvitation.share.role.label"
                                                        default="Permission"/></label>

                <div class="controls">
                    <crm:isAllowedMoreInvitations role="guest">
                        <label class="radio inline"><g:radio value="guest" name="role"/><g:message
                                code="crmRole.role.guest.label" default="Read"/></label>
                    </crm:isAllowedMoreInvitations>
                    <crm:isAllowedMoreInvitations role="user">
                        <label class="radio inline"><g:radio value="user" name="role"/><g:message
                                code="crmRole.role.user.label" default="Edit"/></label>
                    </crm:isAllowedMoreInvitations>
                    <crm:isAllowedMoreInvitations role="admin">
                        <label class="radio inline"><g:radio value="admin" name="role"/><g:message
                                code="crmRole.role.admin.label" default="Admin"/></label>
                    </crm:isAllowedMoreInvitations>
                </div>
            </div>

            <div class="control-group">
                <label class="control-label"><g:message code="crmInvitation.share.msg.label"
                                                        default="Message"/></label>

                <div class="controls">
                    <textarea name="msg" class="span5" cols="40" rows="3"
                              placeholder="${message(code: 'crmInvitation.share.msg.help', default: '')}"></textarea>
                </div>
            </div>

            <div class="modal-footer">

                <crm:button visual="primary" icon="icon-ok icon-white" action="share"
                            label="crmInvitation.button.share.confirm.yes" args="${[bean.toString()]}"/>
                <crm:button type="url" icon="icon-remove" href="#"
                            label="crmInvitation.button.share.confirm.no" args="${[bean.toString()]}"
                            data-dismiss="modal"/>
            </div>
        </g:form>
    </div>
</div>
