/*
* Copyright (c) 2013 Goran Ehrsson.
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
*/
import grails.plugins.crm.invitation.CrmInvitation

/**
 * GR8 CRM Invitation Plugin.
 */
class CrmInvitationGrailsPlugin {
    def groupId = "grails.crm"
    def version = "1.4.0"
    def grailsVersion = "2.4 > *"
    def dependsOn = [:]
    def loadAfter = ['crmCore']
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]
    def title = "GR8 CRM Invitation Plugin"
    def author = "Goran Ehrsson"
    def authorEmail = "goran@technipelago.se"
    def description = "Invite users to GR8 CRM applications"
    def documentation = "http://grails.org/plugin/crm-invitation"
    def license = "APACHE"
    def organization = [name: "Technipelago AB", url: "http://www.technipelago.se/"]
    def issueManagement = [system: "github", url: "https://github.com/goeh/grails-crm-invitation/issues"]
    def scm = [url: "https://github.com/goeh/grails-crm-invitation"]

    def features = {
        crmInvitation {
            description "Invite users to shared information"
            link controller: "crmInvitation", action: "index"
            permissions {
                guest "crmInvitation:index,accept,deny"
                partner "crmInvitation:index,accept,deny"
                user "crmInvitation:index,accept,deny,cancel"
                admin "crmInvitation:*"
            }
            statistics { tenant ->
                def total = CrmInvitation.countByTenantId(tenant)
                def updated = CrmInvitation.countByTenantIdAndDateCreatedGreaterThan(tenant, new Date() - 31)
                def usage
                if (total > 0) {
                    def tmp = updated / total
                    if (tmp < 0.1) {
                        usage = 'low'
                    } else if (tmp < 0.3) {
                        usage = 'medium'
                    } else {
                        usage = 'high'
                    }
                } else {
                    usage = 'none'
                }
                return [usage: usage, objects: total]
            }
        }
    }
}
