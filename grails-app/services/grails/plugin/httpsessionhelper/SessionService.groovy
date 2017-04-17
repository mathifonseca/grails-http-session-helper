package grails.plugin.httpsessionhelper

import javax.annotation.PostConstruct
import javax.servlet.http.HttpSession

import org.codehaus.groovy.grails.web.util.WebUtils

class SessionService {

    static transactional = false

    private def attributes

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
            log.error "SESSION_ERROR | Error while loading attributes from config -> ${ex.message}"
        }
    }

    private boolean validateAttribute(String name) {

        def attr = attributes?."$attrName"

        if (!attr) {
            log.error "SESSION_ERROR | Attribute with name \"${name}\" is not registered in config"
        }

        return attr

    }

    private def propertyMissing(String name) {

        boolean validAttribute = validateAttribute(name)

        if (validAttribute) {

            return getAttribute(name)

        }

        return null

    }

    private def propertyMissing(String name, value) {

        boolean validAttribute = validateAttribute(name)

        if (validAttribute) {

            setAttribute(name, value)

        }

    }

    private def methodMissing(String name, args) {

        if (!name || name.size() < 4) {

            log.error "SESSION_ERROR | Incorrect method name. Must start with get, set or del followed by attribute name."

        }

        String attrName = name[3..-1]

        def attr = attributes?."$attrName"

        boolean validAttribute = validateAttribute(attr)

        if (validAttribute) {

            if (name.startsWith('get')) {

                return getAttribute(attr)

            } else if (name.startsWith('set')) {

                setAttribute(attr, args[0])

            } else if (name.startsWith('del')) {

                deleteAttribute(attr)

            }

        }

        return

    }

    private def getAttribute(String attrName) {
        try {

            def value

            try {

                value = session.getAttribute(attrName)

                log.debug "GET | attr = ${attrName} | value = ${value}"

            } catch (ex) {

                log.error "SESSION_ERROR | Error while getting attribute from session -> ${ex.message}", ex

            }

            return value

        } catch (Exception ex) {
            log.error "SESSION_ERROR | Could not get attribute from session -> ${ex}", ex
        }
    }

    private void setAttribute(String attrName, value) {
        try {
            log.debug "SET | attr = ${attrName} | value = ${value}"
            session.setAttribute(attrName, value)
        } catch (Exception ex) {
            log.error "SESSION_ERROR | Could not set attribute in session -> ${ex}", ex
        }
    }

    private void deleteAttribute(String attrName) {
        try {
            log.debug "DEL | attr = ${attrName}"
            session.removeAttribute(attrName)
        } catch (Exception ex) {
            log.error "SESSION_ERROR | Could not remove attribute from session -> ${ex}", ex
        }
    }

}