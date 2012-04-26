/*
 * Copyright (c) 2012 Goran Ehrsson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * under the License.
 */

package grails.plugins.crm.invitation

import grails.plugins.crm.core.TenantEntity

@TenantEntity
class CrmInvitation {

    public static final int CREATED = 0
    public static final int SENT = 1
    public static final int ACCEPTED = 2
    public static final int DENIED = 8
    public static final int EXPIRED = 9

    // TODO only CREATED and SENT is necessary if instance is logged and removed when accepted, denied or expired.

    String guid
    String ref
    String sender
    String receiver
    //String message
    int status
    Date dateCreated

    static constraints = {
        guid(maxSize: 40, blank: false, unique: true)
        ref(maxSize: 80, nullable: true)
        sender(maxSize: 80, blank: false) // username of sender
        receiver(maxSize: 80, blank: false, email: true) // email of invited user
        //message(maxSize: 2000, nullable: true, widget: 'textarea') // optional message to invited
        status(inList: [CREATED, SENT, ACCEPTED, DENIED, EXPIRED])
    }

    static namedQueries = {
        pending {
            eq('status', CREATED)
        }
    }

    def beforeValidate() {
        if (!guid) {
            guid = java.util.UUID.randomUUID().toString()
        }
    }
}
