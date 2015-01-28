package grails.plugin

import org.codehaus.groovy.grails.web.util.WebUtils
import javax.annotation.PostConstruct

class SessionService {

    static transactional = false

    def attributes

    private def getSession() {
        WebUtils.retrieveGrailsWebRequest().getSession()
    }

    @PostConstruct
    private void init() {

        try {

            attributes = Holders.config.session.attributes

            log.info "SessionService initialized with ${attributes.size()} attributes"

        } catch (Exception ex) {

            log.error "Error while loading attributes from config -> ${ex.message}"

        }

    }

    private def methodMissing(String name, args) {

        if (!name || name.size() < 4) {

            log.error "Incorrect method name. Must start with get, set or del followed by attribute name."

        } else {

            def attrName = name[3..-1].toString()

            def attr

            if (attributes) {
            	attr = attributes[attrName]
            	if (!attr) log.error "Attribute with name \"${attrName}\" is not registered in config"
           	} else {
           		attr = attrName
			}

            if (attr) {

                if (name.startsWith('get')) {

                    def value 

                    try {

                        value = getAttribute(attr)

                        log.debug "GET | attr = ${attr} | value = ${value}"

                    } catch (Exception ex) {

                        log.error "Error while getting attribute from session -> ${ex.message}"

                    }

                    return value

                } else if (name.startsWith('set')) {

                    log.debug "SET | attr = ${attr} | value = ${args[0]}"

                    setAttribute(attr, args[0])

                } else if (name.startsWith('del')) {

                    log.debug "DEL | attr = ${attr}"
                    
                    deleteAttribute(attr)

                } else if (!name || name.size() < 4) {

                    log.error "Incorrect method name. Must start with get, set or del."

                }

            }

        }

    }

    private def getAttribute(String attrName) {
        try {
            return session.getAttribute(attrName)
        } catch (Exception ex) {
            log.error "Could not get attribute from session -> ${ex}"
        }
    }

    private def setAttribute(String attrName, value) {
        try {
    	    session.setAttribute(attrName, value)
        } catch (Exception ex) {
            log.error "Could not set attribute in session -> ${ex}"
        }
    }

    private def deleteAttribute(String attrName) {
        try {
            session.removeAttribute(attrName)
        } catch (Exception ex) {
            log.error "Could not remove attribute from session -> ${ex}"
        }
    }

}