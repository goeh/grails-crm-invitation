<g:form action="share">
    <input type="hidden" name="id" value="${tenant.id}"/>
    <input type="hidden" name="referer" value="${request.forwardURI - request.contextPath}#invite"/>

    <p><g:message code="crmInvitation.share.message" args="${[tenant.name]}"/></p>

    <div class="control-group">
        <label class="control-label">E-postadress</label>

        <div class="controls">
            <input type="email" name="email" class="span4"
                   placeholder="E-postadress till den du vill bjuda in..."/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">Behörighet</label>

        <div class="controls">
            <label class="radio inline"><g:radio value="guest" name="role" checked="checked"/>Läsa</label>
            <label class="radio inline"><g:radio value="user" name="role"/>Ändra</label>
            <label class="radio inline"><g:radio value="admin" name="role"/>Administrera</label>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">Meddelande (frivilligt)</label>

        <div class="controls">
            <textarea name="msg" placeholder="Personligt meddelande..." class="span5" cols="40"
                      rows="3"></textarea>
        </div>
    </div>

    <div class="form-actions">
        <crm:button visual="primary" icon="icon-ok icon-white" action="share"
                    label="account.button.share.confirm.yes" args="${[tenant.name]}"/>
    </div>
</g:form>
