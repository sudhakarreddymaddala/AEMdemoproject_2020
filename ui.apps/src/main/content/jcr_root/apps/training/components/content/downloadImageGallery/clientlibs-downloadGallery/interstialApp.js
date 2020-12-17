/**
 * FP product page.js
 * Version 1.0
 */
(function(global, PLAYAEM) {
    var fpDownloadApps = {
        el: '.fpDownloadapps-module',
        bindingEventsConfig: function() {
            if (PLAYAEM.isLoaded){
	            return;
            }	
        },

        modalHandling: function(){
            $(window).on('load',function(e){
            if (!PLAYAEM.isMobile) {
                var targetId=$('.config-app').attr('data-modalid');
                $('.config-app').attr("data-target", targetId);
            }
            if (PLAYAEM.isMobile) {
                var userAgent = navigator.userAgent || navigator.vendor || window.opera;
                // Windows Phone detection
                /* if (/windows phone/i.test(userAgent)) {
                    console.log("Windows Phone");
                } */
                
                $('.config-app').each(function (appClass) {
                    // android detection
                    if (/android/i.test(userAgent)) {
                        //console.log("Android");
                        itemApp = $(this).attr('data-androidappurl');
                        $(this).attr('href',itemApp);
                    }
                    // iOS detection
                    if (/iPad|iPhone|iPod/.test(userAgent) && !window.MSStream) {
                        //console.log("iOS");
                        itemApp = $(this).attr('data-iosappurl');
                        $(this).attr('href',itemApp);
                    }
                });
                return 'unknown';
            }
        });
        },
        truncateTitle: function () {
            if (PLAYAEM.isMobile) {
                $(".txt-truncate").each(function(i){
                    len=$(this).text().length;
                    if(len>65)
                    {
                    $(this).text($(this).text().substr(0,65)+'…');
                    }
                });
            }
        },
        
        render: function() {
            this.truncateTitle();
        },

        init: function() {
            if (!PLAYAEM.isDependencyLoaded || !$(this.el).length || PLAYAEM.retailers) return;
            PLAYAEM.bindLooping(this.bindingEventsConfig(), this);
            this.render();
            this.modalHandling();
        }
    }
    
    fpDownloadApps.init();
    PLAYAEM.fpDownloadApps = fpDownloadApps;
    document.addEventListener('DOMContentLoaded', function() {
        if (!PLAYAEM.isDependencyLoaded) {
            fpDownloadApps.init();
        }
    }, false);
}(window, PLAYAEM));
