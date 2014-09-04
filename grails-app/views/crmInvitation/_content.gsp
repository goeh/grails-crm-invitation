<crm:user username="${invitation.sender}">
    <h3>${(name ?: invitation.sender).encodeAsHTML()}
        <small><g:formatDate type="date" date="${invitation.dateCreated}"/></small>
    </h3>
    <h4>${reference.encodeAsHTML()}</h4>

    <p>
        ${(name ?: invitation.sender).encodeAsHTML()} &lt;${email?.encodeAsHTML()}&gt; bjuder in dig till
        <g:message code="app.name" default="denna tjänst"/>.
        Genom att acceptera denna inbjudan får du behörighet att ta del av &quot;${reference.encodeAsHTML()}&quot;
        som hanteras av ${(name ?: invitation.sender).encodeAsHTML()}.
    </p>
</crm:user>
