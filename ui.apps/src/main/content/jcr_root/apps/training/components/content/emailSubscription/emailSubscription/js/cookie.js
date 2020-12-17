

/**
 * Cookie.js
 * Version 1.0
 */
(function (global, CRMAEM) {
    var
        cookie = {
            cookiePath: "/",
            prepend: function (name, value, options, isString) {
                var ids = [];
                options = options || {};
                if (!value) {
                    value = '';
                    options.expires = -365;
                } else {
                    value = escape(value);
                }
                if (options.expires) {
                    value += '; expires=' + new Date(options.expires).toUTCString()
                }
                if (options.domain) { value += '; domain=' + options.domain; }
                if (options.path) { value += '; path=' + options.path; }
                // get current cookies
                ids = this.get(name, '', isString);

                // split by _
                if (ids !== undefined && ids !== "" && ids !== null) { ids = isString ? ids.split('^') : ids.split('_'); }
                // insert element from the left
                if (!_.isArray(ids)) { ids = [value]; }
                else {
                    // Don't insert repeated cookies
                    if (!this.inCookie(name, value)) { ids.unshift(value); }
                }
                this.arrayToCookie(name, ids, isString);
            },
            arrayToCookie: function (name, ids, isString) {
                var myDate = new Date();
                myDate.setMonth(myDate.getMonth() + 12);
                var newCookie = isString ? name + '=' + ids.join('^') + '; path=' + this.cookiePath : name + '=' + ids.join('_') + '; path=' + this.cookiePath;
                document.cookie = newCookie;
            },
            set: function (name, ids, days) {
                var expires = "";
                if (days) {
                    var date = new Date();
                    date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
                    expires = "; expires=" + date.toUTCString();
                }
                document.cookie = name + "=" + (ids || "") + expires + "; path=/";
            },
            deleteCookie: function (name) {
                document.cookie = name + '=; Max-Age=-99999999;';
            },
            get: function (name, asArray, isString) {
                var separator;
                var cookies = document.cookie.split(';');
                var tempName;
                var v;

                for (var i = 0; i < cookies.length; i++) {
                    separator = cookies[i].indexOf("=");
                    tempName = $.trim(cookies[i].substr(0, separator));
                    v = cookies[i].substr(separator + 1);
                    if (tempName === name) {
                        if (!asArray) { return unescape(v); }
                        else {
                            if (v.length == 0) { return null; }
                            else { return isString ? unescape(v).split('^') : unescape(v).split('_'); }
                        }
                    }
                }
            },
            getEscapeVal: function (name, asArray, isString) {
                var separator;
                var cookies = document.cookie.split(';');
                var tempName;
                var v;

                for (var i = 0; i < cookies.length; i++) {
                    separator = cookies[i].indexOf("=");
                    tempName = $.trim(cookies[i].substr(0, separator));
                    v = cookies[i].substr(separator + 1);
                    if (tempName === name) {
                        if (!asArray) { return decodeURIComponent(v); }
                        else {
                            if (v.length == 0) { return null; }
                            else { return isString ? decodeURIComponent(v).split('^') : decodeURIComponent(v).split('_'); }
                        }
                    }
                }
            },
            remove: function (name, id, isString) {
                var value;
                var ids;
                var newIds;
                var cookies = document.cookie.split(';');
                var tempName, separator;

                for (var i = 0; i < cookies.length; i++) {
                    separator = cookies[i].indexOf("=");
                    tempName = $.trim(cookies[i].substr(0, separator));
                    value = cookies[i].substr(separator + 1);
                    if (tempName === name) {
                        ids = isString ? value.split('^') : value.split('_');
                        newIds = _.without(ids, id + '');
                        this.arrayToCookie(name, newIds);
                    }
                }
            },
            toggleCookie: function (isActive, cookieName, id) {
                if (id !== undefined) {
                    if (isActive) {
                        this.prepend(cookieName, id, '', '');
                    } else {
                        this.remove(cookieName, id, '');
                    }
                }
            },
            inCookie: function (name, id, isString) {
                if (_.indexOf(this.get(name, true, isString), id + '') > -1) {
                    return true;
                }
                return false;
            }
        }

    CRMAEM.cookie = cookie;
}(typeof window !== "undefined" ? window : this, CRMAEM));
