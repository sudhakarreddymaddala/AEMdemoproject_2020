/**
* AEM.js
* Version 1.0
*/
(function(global) {
    // "use strict";
    var version = 1.0,
        /*isLoaded = false,*/
        glob = typeof global === 'undefined' ? window : global,
        doc = glob.document,
        /*SVG_NS = 'http://www.w3.org/2000/svg',*/
        userAgent = (glob.navigator && glob.navigator.userAgent) || '',
        /*svg = (
        doc &&
        doc.createElementNS &&
        !!doc.createElementNS(SVG_NS, 'svg').createSVGRect
        ),*/
        /*isMS = /(edge|msie|trident)/i.test(userAgent) && !glob.opera,*/
        isFirefox = userAgent.indexOf('Firefox') !== -1,
        isChrome = userAgent.indexOf('Chrome') !== -1,
        CRMAEM = {
            version: version,
            isDependencyLoaded: typeof _ == "function" && typeof $ == "function" ? true : false,
            hasTouch: doc && doc.documentElement.ontouchstart !== undefined,
            isWebKit: userAgent.indexOf('AppleWebKit') !== -1,
            isFirefox: isFirefox,
            isChrome: isChrome,
            isSafari: !isChrome && userAgent.indexOf('Safari') !== -1,
            isTouchDevice: /(Mobile|Android|Windows Phone)/.test(userAgent),
            /*SVG_NS: SVG_NS,*/
            onlyDesktop: window.innerWidth < 1024,
            isMobile: window.innerWidth <= 767,
            win: typeof global === 'undefined' ? window : global,
            bindingEventsConfig: function() {
                var events = {
                    /*"click [data-tracking-id]": "getTrackingValues"*/
                }
                return events;
            },
            /*
            @params
            param1 - bind Element
            param2 - cb function
            */
            bindEvents: function(eventName, el, callBack) {
                if (typeof callBack === 'function') {
                    $(document).on(eventName, el, function(evt) {
                        callBack(this, evt);
                    });
                } else {
                    console.log("Error:CB function not found for this element :" + el);
                }
            },
            bindLooping: function(name, evtParentObj) {
                // Regular expression used to split event strings.
                var eventSplitter = /(\S+)\s(.*)/;
                var i = 0,
                    names, splitKeys;
                for (names = _.keys(name); i < names.length; i++) {
                    splitKeys = names[i].match(eventSplitter).slice(1);
                    if (!_.isEmpty(splitKeys) && !_.isEmpty(name[names[i]]) && typeof evtParentObj[name[names[i]]] == "function") {
                        this.bindEvents(splitKeys[0], splitKeys[1], evtParentObj[name[names[i]]]);
                    } else {
                        console.log("Event Binding failed for " + splitKeys);
                    }
                }
            },
            requestAPICall: function(obj, cb) {
                /*    var self = this;*/
                return $.ajax({
                    type: obj.type,
                    url: obj.url,
                    contentType: 'application/json',
                    data: JSON.stringify(obj.body || ''),
                    success: function(response) {
                        if (typeof cb == "function") {
                            cb(response)
                        }
                    },
                    error: function(errrLog) {
                        if (typeof cb == 'function') {
                            cb(false);
                            console.log(obj.methodName + "API Error!", "error");
                        }
                    }
                });
            },
            getTrackingValues: function(elem, evt) {
                var trackingVal = $(elem).data("trackingId")
                if (_.isEmpty(trackingVal)) {
                    console.log("Warn: Tracking value should not be empty.. ");
                    return;
                }
                var valArr = trackingVal.split('|');
                var obj = {
                    event_name: valArr[0] || '', // action name
                    event_type: 'click', //action event type
                    item_clicked: valArr[1] || '', //category
                    item_subcategory: valArr[2] || '', //sub category element
                    location_name: valArr[3] || ''
                }
                var evtName = valArr[0] && valArr[0].split('-')[0];
                typeof sendToAnalytics == "function" && sendToAnalytics(obj, (evtName && evtName.toLowerCase() == "click" && elem.tagName == "A") ? "button" : evtName);
            },
            render: function() {
                var bodyDesktopImg = new Image();
                var bodyMobileImg = new Image();
                if(typeof $('body').attr('data-desktopimage') !== 'undefined'){
                	bodyDesktopImg.src=$('body').attr('data-desktopimage');
                }
                if(typeof $('body').attr('data-mobileimage') !== 'undefined'){
                	 bodyMobileImg.src=$('body').attr('data-mobileimage');
                }
                var styleElem = document.head.appendChild(document.createElement("style"));
                styleElem.innerHTML = '.bg-img:before {background-image: url('+$('body').attr('data-desktopimage')+')} @media screen and (max-width:767px) { .bg-img:before {background-image: url('+$('body').attr('data-mobileimage')+')}}';
                // if(window.innerWidth <= 767) {
                //     var bgMobAttr = $('body').attr('data-mobileimage');
                //     var bgImg = "";
                //     bgMobAttr ? bgImg = $('body').attr('data-mobileimage') : bgImg = $('body').attr('data-desktopimage');
                //     styleElem.innerHTML = '.bg-img:before {background: '+bgImg+'}';
                //     // $('body').css("background-image", 'url('+bgImg+')');
                // } else {
                //     // $('body').css({"background-image": 'url('+$('body').attr('data-desktopimage')+')'});
                //     //$('body').css({"background-image": 'url('+$('body').attr('data-desktopimage')});
                // }
                if($('body').attr('data-desktopimage') || $('body').attr('data-mobileimage')) {
                    $('body').addClass("bg-img");
                }

            },
            resize: function() {
                var _self = this;

                $(window).on("resize load ", function() {
                       if(window.innerWidth <= 767) {
                          $("footer").css("margin-top",0);
                        }else {
                          _self.setFooter();
                        }
                });


            },
            setFooter: function(){
                var rootHeight = $(".root").outerHeight();
                var windowHeight = $(window).outerHeight();
                var marginTop = parseInt(windowHeight) - parseInt(rootHeight);
                if(marginTop > 0) {
                    $(".footer").css("margin-top",marginTop);
                }

            },
            init: function() {
                CRMAEM.bindLooping(CRMAEM.bindingEventsConfig(), CRMAEM);
                this.render();
                this.resize();
            }
        }
    global = global || window;
    global.CRMAEM = CRMAEM;
    CRMAEM.init();
}(window));
