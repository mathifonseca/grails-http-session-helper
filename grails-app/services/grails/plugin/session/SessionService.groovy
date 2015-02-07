package grails.plugin.session

import javax.annotation.PostConstruct
import javax.servlet.http.HttpSession

import org.codehaus.groovy.grails.web.util.WebUtils

class SessionService {

    static transactional = false

    private attributes

    def grailsApplication

    private HttpSession getSession() {
        WebUtils.retrieveGrailsWebRequest().session
    }

    @PostConstruct
    private void init() {
        try {

            attributes = grailsApplication.config.session.attributes

            log.info "Initialized with ${attributes.size()} attributes"

        } catch (ex) {
            log.error "Error while loading attributes from config -> ${ex.message}"
        }
    }

    private methodMissing(String name, args) {

        if (!name || name.size() < 4) {

            log.error "Incorrect method name. Must start with get, set or del followed by attribute name."
            return

        }

        String attrName = name[3..-1]

        def attr

        if (attributes) {
            attr = attributes[attrName]
            if (!attr) log.error "Attribute with name \"${attrName}\" is not registered in config"
           } else {
               attr = attrName
        }

        if (!attr) {
            return
        }

        if (name.startsWith('get')) {

            def value

            try {

                value = getAttribute(attr)

                log.debug "GET | attr = ${attr} | value = ${value}"

            } catch (ex) {
                log.error "Error while getting attribute from session -> ${ex.message}", ex
            }

            return value

        }

        if (name.startsWith('set')) {

            log.debug "SET | attr = ${attr} | value = ${args[0]}"

            setAttribute(attr, args[0])
            return

        }

        if (name.startsWith('del')) {

            log.debug "DEL | attr = ${attr}"

            deleteAttribute(attr)
        }
    }

    private getAttribute(String attrName) {
        try {
            return session.getAttribute(attrName)
        } catch (Exception ex) {
            log.error "Could not get attribute from session -> ${ex}", ex
        }
    }

    private void setAttribute(String attrName, value) {
        try {
            session.setAttribute(attrName, value)
        } catch (Exception ex) {
            log.error "Could not set attribute in session -> ${ex}", ex
        }
    }

    private void deleteAttribute(String attrName) {
        try {
            session.removeAttribute(attrName)
        } catch (ex) {
            log.error "Could not remove attribute from session -> ${ex}", ex
        }
    }
}
