/**
 * video-gallery-player.js
 * Version 1.0
 */
(function(global, PLAYAEM, ooyala) {

    // 'use strict';
    var storageName = "videoAPIDatas",
        isPlayerElementLen,
        pageList,
        autoplaycheckCnt = 0,
        self,
        isAutoplay,
        indx = 0,
        isIOSDevice = /iPad|iPhone|iPod/.test(navigator.userAgent) && !window.MSStream;
    var videoplayer = {
        el: '[id="video-gallery-player-component"]',
        gridEl: '#player-gallery-thumbnail-datas',
        bindingEventsConfig: function() {
            var events = {
                "click .play-list li": "thumbnailAction",
                "click #play-list-datas li": "thumbnailAction",
                "click .video-player-container .slide-btn": "videoSlide"
            };
            return events;
        },
        APICall: function() {
            if(!isPlayerElementLen){
                isPlayerElementLen = pageList.find(".featured-promo").length ? true : false;
            }
            if (isPlayerElementLen && $('.play-list').length != 0) {
                    _.each($(".play-list"), function(item) {
                        $(item).find("li:eq(0)")[0].click();
                    });
                // $(".play-list li:eq(0)")[0].click();
            }
			if(!PLAYAEM.onlyDesktop) {
				if($('.video-rightscroll').length >=1)
				$('.video-rightscroll .videos-gallery-playlist').height($('.video-rightscroll #main-player-container-1').height());
			}
        },
        thumbnailAction: function(ele, evt) {
            var isPlayerAvailable= $(ele).parents(".gallery-tile").data('playerAvailable');
            if (!isPlayerElementLen || (isPlayerAvailable!=undefined && !isPlayerAvailable)) {
                var $hrefElem = $(ele).find("a:first");
                var targetAttr = $hrefElem.data('detailLink');
                $hrefElem.attr("href", targetAttr);
                return;
            }
            evt && evt.preventDefault();
            if (ele && $(ele).hasClass("active")) {
                return false;
            }
            var self = PLAYAEM.videoplayer;
            var index = $(ele).data("index");
            // var self = PLAYAEM.videoplayer;
            // var index = $(ele).data("index");
            var $parentUL = $(ele).parents(".play-list");
            var $currentPlayer = $parentUL.parents(".video-component").find(".featured-promo");
            indx = index;
            var videoId = $(ele).data("videoId");
            var videoTitle = $(ele).find(".tile-content").text();
            var videoDesc = $(ele).find(".tile-desc").text();
            $parentUL.find('li').removeClass("active");
            $parentUL.find("li[data-index=" + index + "]").addClass("active");
            self.updateVideoPlayer(videoId, videoTitle, videoDesc, $currentPlayer);
        },
        enableAutoplay: function() {
            indx = indx + 1;
            if (indx == $('#videos-gallery .play-list li').length) {
                indx = 0;
            }
            $(".play-list li[data-index=" + indx + "]")[0].click();
        },
        updateVideoPlayer: function(videoId, videoTitle, videoDesc, player) {
            var self = this;
            // var isAutoplay;
            var $playerContainer = player;
            var $parentContainer = $playerContainer.closest(".video-player-container");
            var playerId = $playerContainer.attr('id');
            var playerIndx = $playerContainer.data('videoIndex');
            $playerContainer.attr('data-video-id', videoId);
            if (!this.featuredPlayers[playerIndx]) {
                isAutoplay = $playerContainer.data('autoplay');
                ooyala.waitForElement(function(pluginLoaded) {
                    if (pluginLoaded) self.featuredPlayers[playerIndx] = ooyala.startOoyalaPlayer(videoId, playerId);
                        setTimeout(function() {
                            isAutoplay && !self.featuredPlayers[playerIndx].isPlaying() && self.isScrolledIntoView($playerContainer) > 0 && !isIOSDevice && self.videoPlayonLoad(function() {
                                !PLAYAEM.isTouchDevice() && self.featuredPlayers[playerIndx].play();
                            }, 10);
                        setTimeout(function() {
                                // accessibility
                            $('iframe[name^=Ooyala],iframe[src^="https://player.ooyala.com"]').attr('title', 'ooyala iframe');
                            $('.cd-prev').attr('aria-label', 'Previous');
                            $('.cd-next').attr('aria-label', 'Next');
                            $('.oo-volume').attr('aria-label', 'Volume');
                            $('.oo-control-bar').attr('aria-label', 'Video Player');
                            $('.oo-fullscreen').attr('aria-label', 'Screen Resize');
                        }, 1100);
                    });
                });
            } else {
                this.featuredPlayers[playerIndx].setEmbedCode(videoId);
                $('html, body').animate({
                    scrollTop: $playerContainer.offset().top - $("header").height() - 100 //#DIV_ID is an example. Use the id of your destination on the page
                }, 500);
                setTimeout(function() {
                    !isIOSDevice && self.featuredPlayers[playerIndx].play()
                }, 10);
            }
            $parentContainer.find("#video-player-title").html(videoTitle);
            $parentContainer.find("#video-player-desc").html(videoDesc);
            $playerContainer.attr('aria-label', videoTitle + ' Video');

        },
        videoPlayonLoad: function(callBack) {
            var self = this;
            window.setTimeout(function() {
                if (self.player.getState() == "ready") {
                    callBack(true);
                } else {
                    autoplaycheckCnt <= 10 && self.videoPlayonLoad(callBack);
                    autoplaycheckCnt++;
                }
            }, 500)
        },
        isScrolledIntoView: function(elem) {
            var x = elem.offsetLeft,
                y = elem.offsetTop,
                w = elem.offsetWidth,
                h = elem.offsetHeight,
                r = x + w, //right
                b = y + h, //bottom
                visibleX, visibleY, visible;

            visibleX = Math.max(0, Math.min(w, window.pageXOffset + window.innerWidth - x, r - window.pageXOffset));
            visibleY = Math.max(0, Math.min(h, window.pageYOffset + window.innerHeight - y, b - window.pageYOffset));

            return visibleX * visibleY / (w * h);
        },
        videoSlide: function(ele, evt) {
            evt.preventDefault();
            var self = PLAYAEM.videoplayer,
                actionName = $(ele).data("action");
            if (actionName == "prev") {
                if (indx == 0) {
                    indx = $('#videos-gallery .play-list li').length;
                }
                indx = indx - 1;
            } else if (actionName == "next") {
                indx = indx + 1;
                if (indx > $('#videos-gallery .play-list li').length - 1) {
                    indx = 0;
                }
            }
            //$(".play-list li[data-index=" + indx + "]")[0].click();
			$(ele).parents(".video-component").find(".play-list li[data-index=" + indx + "]")[0].click();
        },
        heightSync: function(elem) {
            var max = -1;
            var $heightElem = $(elem).find(".tile-content");
            $(elem).find('img').imagesLoaded(function() { // image ready
                _.each($heightElem, function(el) {
                    var height = $(el).innerHeight();
                    max = height > max ? height : max;
                });

                $heightElem.css('height', max + 'px');
            });
            return;
        },
        createSinglePlayer: function(playerId) {
            var ele = $("#" + playerId)[0],
                dataAttr = ele.dataset,
                indx = dataAttr.elemIndex || 0,
                videoId = dataAttr.videoId,
                isAutoplay = dataAttr.autoplay || false,
                trackingVal,
                englishTxt;
            if (videoId == undefined) {
                console.log("video attribute not found. Element Id is: " + playerId);
                return;
            }
            ooyala.waitForElement(function(pluginLoaded) {
                if (pluginLoaded) {
                    englishTxt = $("#videotext-always-english").val();
                    self.singlePlayer[indx] = ooyala.playerCreate(playerId, videoId);
                    self.singlePlayer[indx].mb.subscribe(OO.EVENTS.PLAYING, 'playing', function(event) {
                        trackingVal = "Video Text Section|About Alpha Training Blue|Play|Video Player";
                        PLAYAEM.getTrackingValues('', '', trackingVal);
                        $('.oo-play-pause').attr('aria-label', 'Pause');
                    });
                    self.singlePlayer[indx].mb.subscribe(OO.EVENTS.PAUSED, 'paused', function(event) {
                        trackingVal = "Video Text Section|About Alpha Training Blue|Pause|Video Player";
                        PLAYAEM.getTrackingValues('', '', trackingVal);
                        $('.oo-play-pause').attr('aria-label', 'Play');
                    });
                }
                if (!PLAYAEM.isTouchDevice() && isAutoplay && !self.singlePlayer[indx].isPlaying() && self.isScrolledIntoView(ele) > 0) {
                    setTimeout(function() {
                        self.singlePlayer[indx].play();
                    }, 10);
                }
            });
        },
        createIDsforPlayers: function() {
            var $videoEle = pageList.find(".featured-promo");
            if (!$videoEle.length) {
                console.log("Warn: featured video player not exists..");
                return;
            }
            _.each($videoEle, function(item, indx) {
                $(item).attr({
                    'id': 'main-player-container-' + (indx + 1),
                    'data-video-index': indx
                })
                // item.setAttribute('id', 'main-player-container-' + (indx + 1))
                // item.setAttribute('data-video-index', indx);
            });
        },
        initSinglePlayer: function() {
            var $targetElem = $(".single-video-player");
            if (!$targetElem.length) return;
            var elemId;
            self.singlePlayer = self.singlePlayer || [];
            _.each($targetElem, function(item, indx) {
                elemId = 'single-player-' + indx;
                $(item).attr({
                    'data-elem-index': indx,
                    'id': elemId
                });
                self.createSinglePlayer(elemId);
            });
        },
        playerScrollAction: function(curVal, indx, isVisible) {
            isAutoplay = $(curVal).find('.featured-promo').data('autoplay');
            var curPlayer = self.featuredPlayers[indx];
            if (curPlayer == undefined) {
                return; // player not initiated it seems
            }
            if (!ooyala.isManuallyPaused[indx] && isVisible == undefined) {
                if (curPlayer && !curPlayer.isPlaying() && ((curPlayer.getState() == "ready" && isAutoplay && !PLAYAEM.isTouchDevice()) || curPlayer.getState() == "paused")) {
                    !PLAYAEM.isTouchDevice() && curPlayer.play();
                }
            } else {
                if (curPlayer && curPlayer.isPlaying()) {
                    setTimeout(function() {
                        if (!curPlayer.isFullscreen() && curPlayer.getState() != "paused") {
                            curPlayer.pause();
                            setTimeout(function() {
                                ooyala.isManuallyPaused[indx] = false;
                            }, 100)
                        }
                    }, 1000)
                }
            }
        },
        init: function() {
            if (!PLAYAEM.isDependencyLoaded || !$(this.el).length || PLAYAEM.videoplayer) return;
            self = this;
            self.featuredPlayers = [];
            PLAYAEM.bindLooping(this.bindingEventsConfig(), this);
            this.heightSync($(".play-list li"));
            pageList= $("#pagename-home,#pagename-instructions");
            isPlayerElementLen = pageList.find(".featured-promo").length ? true : false;
            self.initSinglePlayer();
        }
    };

    videoplayer.init();
    PLAYAEM.videoplayer = videoplayer


    document.addEventListener('DOMContentLoaded', function() {
        var parentObj = PLAYAEM.videoplayer;
        parentObj.createIDsforPlayers();
        parentObj.APICall();
        if (!PLAYAEM.isDependencyLoaded) {
            videoplayer.init();
        }
        var playerContainer = pageList.find(".video-player-container");
        var scrollIn = [];
        $(window).scroll(function() {
            if(parentObj.featuredPlayers != undefined && parentObj.featuredPlayers != null && !isIOSDevice){
                for (var i = 0; i < playerContainer.length; i++) {
                    var scrollPos = parentObj.isScrolledIntoView(playerContainer[i]);
                    // console.log(scrollPos);
                    if (scrollPos > 0.2) {
                        if (scrollIn[i] != true) {
                            parentObj.playerScrollAction(playerContainer[i], i);
                            scrollIn[i] = true;
                            // console.log("event scroll");
                            break;
                        }
                    } else if (scrollIn[i] == true) {
                        parentObj.playerScrollAction(playerContainer[i], i, false);
                        scrollIn[i] = false;
                    }
                }
            }
        });
    }, false);
}(window, PLAYAEM, function() {

    var pluginLoaded = false,
        pluginCheckCnt = 0;
    var ooyalaPlayer = {
        isManuallyPaused: [],
        ooVideoSetters: function() {
            if (typeof OO === "undefined") return;
            OO.ready(function() {
                //player ready
                pluginLoaded = true;
            });
        },
        playerEmbedCode: function(_player, _contentid) {
            _player.setEmbedCode(_contentid);
            !isIOSDevice && _player.play();
        },
        waitForElement: function(callBack) {
            var self = this;
            window.setTimeout(function() {
                if (pluginLoaded) {
                    callBack(true);
                } else {
                    pluginCheckCnt++;
                    if (pluginCheckCnt > 8) {
                        console.log("Ooyala Plugin not available here.. ");
                        return;
                    }
                    self.waitForElement(callBack);
                }
            }, 500)
        },
        playerCreate: function(elementId, externalId) {
            var elemId = elementId || "main-player-container",
                contentId = (externalId.length >= 32 || externalId.indexOf('extId:') != -1) ? externalId : "extId:" + externalId,
                player = OO.Player.create(elemId, contentId, PLAYAEM.ooParams);
            return player;
        },
        startOoyalaPlayer: function(externalId, elemId) {
            var self = this;
            var $videoElem,
                videoIndex;
            if (!pluginLoaded) {
                console.log("ooyala plugins not loaded..");
                return;
            }
            var englishTxt = $("#filter-alway-english").val();
            var contentId = (externalId.length >= 32 || externalId.indexOf('extId:') != -1) ? externalId : "extId:" + externalId;
            player = self.playerCreate(elemId, contentId);

            player.mb.subscribe(OO.EVENTS.PLAYING, 'playing', function(event) {
                var trackingVal = "video Gallery section|" + this.mb._interceptArgs.contentTreeFetched[0].title + "|play|video Player";
                PLAYAEM.getTrackingValues('', '', trackingVal);
                $('.oo-play-pause').attr('aria-label', 'Pause');
            });
            player.mb.subscribe(OO.EVENTS.PAUSED, 'paused', function(evt, player, id) {
                var trackingVal = "video Gallery section|" + this.mb._interceptArgs.contentTreeFetched[0].title + "|pause|video Player";
                PLAYAEM.getTrackingValues('', '', trackingVal);
                $('.oo-play-pause').attr('aria-label', 'Play');
                $videoElem = pageList.find('.video-player-container .featured-promo[data-video-id=' + this.mb._interceptArgs.setEmbedCode[0] + ']');
                videoIndex = $videoElem.length && $videoElem.data('videoIndex');
                self.isManuallyPaused[videoIndex] = true;
            });
            player.mb.subscribe(OO.EVENTS.PLAYED, 'completed', function(event) {
                PLAYAEM.videoplayer.enableAutoplay();
                PLAYAEM.verticalListHeight();

            });

            return player;

        }

    };

    ooyalaPlayer.ooVideoSetters();

    return ooyalaPlayer;

}()));
