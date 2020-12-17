/**
   * crm-form.js
   * Version 1.0
   */
  (function (global, CRMAEM, cookie) {
      var form = {
          el: '#crm-form',
          clientId : document.getElementById("formClientId").value,
          secretId : document.getElementById("formClientSecret").value,
          countryCode : document.getElementById("formCountryCode").value,
          sourceId : document.getElementById("formSourceId").value,
          locale : document.getElementById("formLocale").value,
          pageLocale : document.getElementById("pageLocale").value,
          noSubscriptionId : document.getElementById("nosubscriptionId").value,
          apiBaseUrl : document.getElementById("formApiBaseUrl").value+"api/",
          tokenKeyName : 'mrs_token',
          belowChildAge : 10,
          cookieExpireDay : 1,
          cookieExpireDate : new Date().setDate(new Date().getDate() + 1),
          existEmail:'',
          childShowFlag: false,
          childDOBCount:0,
          childDetailClone: "",
          dobRequired: false,
          maxChildren:0,
          apiConfig : function(name){
              var obj = {
                  "getToken" : {
                      "url" : this.apiBaseUrl + "token/clienttoken",
                      "body" : { clientId: this.clientId, clientSecret: this.secretId },
                      "type": "POST",
                      "contentType": 'application/x-www-form-urlencoded'
                  },
                  "acquistionAPI" : {
                      "url" : this.apiBaseUrl + "registration/emailacquisition",
                      "body" : "",
                      "type": "POST",
                      "contentType": 'application/json',
                      "beforeSend" : 'Bearer ' + this.clientTokenKey
                  },
                  "getAPIErrorMessage" : {
                    "url" : "/apierror.json",
                      "body" : "",
                      "type": "get",
                      "contentType": 'application/json'
                  },
                  "getGeoDetection" : {
                      "url" : "//geodetection.svc.mattelcloud.com/api/geoipcode",
                      "type": "get"
                  }
              }
              return obj[name];
          },
          ajaxCall : function(obj,cb){
              $.ajax({
                  url: obj.url,
                  data: obj.body || '',
                  type: obj.type,
                  contentType: obj.contentType || "application/json",
                   beforeSend: function(xhr) {
                      if(obj.beforeSend){
                          xhr.setRequestHeader('Authorization', obj.beforeSend);
                      }
                  },
                  success: function(data) {
                  if(data.status == 'SUCCESS') {
					$('.thank-msg').show();
                    $('.form-component').hide();

				   }
                  if (typeof cb == "function") cb(data);

              		},
                  error: function(xhr, textStatus, errorThrown) {if (typeof cb == "function") cb(false,xhr.responseJSON || errorThrown);}
              });
          },
          bindingEventsConfig: function () {
              var events = {
                  "keyup #crm-form .input_field": "inputFocus",
                  "mouseover #crm-form .input_field": "inputHover",
                  "change #crm-form input[type='radio']": "radioFocus",
                  "click #crm-form #crm-form-submit" : "formSubmit",
                  "click #crm-form #crm-form-reset" : "formReset",
                  "change #shortCountryCode" : "updateCountryCodeValue",
                  "change #countryDropdown" : "changeStateDropdown",
                  "click #crm-form #addChild" : "addChild"
              }
              return events;
          },
          addChild : function(ele,evt){
              var self = CRMAEM.MRSForm;
              
              if(!self.childShowFlag) {
                var chidDetails = $(self.el).find("#child-details-wrapper .child-details-fieldset").eq(0);
                  self.childDetailClone = chidDetails.clone();
                  chidDetails.removeClass("hide");
                  if(self.dobRequired) {
                    chidDetails.find(".child-dob").parents(".form-group").addClass("required");
                    chidDetails.find(".child-dob input").attr("required",true);
                  }                  
                  self.childShowFlag = true;
                  self.childDOBCount +=1;
              } else {
                 $(self.el).find(".add-child-option").before("<fieldset class='child-details-fieldset'>"+self.childDetailClone.html()+"</fieldset>");
                  var  childParent = $(self.el).find("#child-details-wrapper .child-details-fieldset").eq($(self.el).find("#child-details-wrapper .child-details-fieldset").length-1);
                  self.changeFieldId(childParent);
                  self.childDOBCount +=1;
              }
              self.checkMaxChildren();
              evt.preventDefault();
          },
          checkMaxChildren: function(){
            if($(this.el).find("#child-details-wrapper .child-details-fieldset").length == this.maxChildren){
                $(this.el).find("#addChild").addClass("hide");
            } else {
                $(this.el).find("#addChild").removeClass("hide");
            }
          },
          changeFieldId : function(el){
            var self = CRMAEM.MRSForm;
            el.find(".child-data").each(function(){                
                if($(this).hasClass("child-gender") || $(this).hasClass("child-relation")){                    
                    $(this).find(".item-list").each(function(){
                        var nameProp = $(this).find("input").attr("name")+self.childDOBCount;
                        var idProp = $(this).find("input").attr("id")+self.childDOBCount;
                        var forProp = $(this).find("label").attr("for")+self.childDOBCount;
                        $(this).find("input").attr({name: nameProp,id: idProp});
                        $(this).find("label").attr("for",forProp);
                    });
                }else {
                    var nameProp = $(this).find("input").attr("name")+self.childDOBCount;
                    var idProp = $(this).find("input").attr("id")+self.childDOBCount;
                    var forProp = $(this).prev("label").attr("for")+self.childDOBCount;
                    if(self.dobRequired) {
                        $(this).parents(".form-group").addClass("required");
                        $(this).find("input").attr("required",true);
                    }
                    $(this).find("input").attr({name: nameProp,id: idProp});
                    $(this).prev().attr("for",forProp);
                }
            });
            var datebinding = this.bindDatePicker(true);
              datebinding && datebinding
                  .keyup(function(ele){
                      self.dobValidation(ele,this)
                  }).change(function(ele){
                      self.dobValidation(ele,this)
                  });
            
            
          },
          updateCountryCodeValue : function(ele){
              var $parentElem = $(ele).closest(".relative-dropdown-container"),
                  $targetElem = $parentElem.find("#relative-country-code");
  
              $targetElem.text($(ele).find(":selected").data('code'));
          },
          formSubmit : function(elem,evt){
              var self = CRMAEM.MRSForm,
                  $parentElem = $(elem).parents(".form-action-btn");
              self.showLoading();
              self.validateForm(elem, function(params) {
                  evt.preventDefault();
                  if(!params){
                      console.log("field validation error..")
                      setTimeout(function(){self.hideLoading()},500);
                      return false;
                  }
                  if (self.clientTokenKey == null) {
                      self.getClientToken( self.sendParamsToAPI, params);
                      $('html, body').animate({scrollTop: ($parentElem.offset().top)}, 'slow');
                      $parentElem.addClass("api-fails");
                      setTimeout(function(){self.hideLoading()},500);
                      return false;
                  } else{
                      var ajaxSettings = self.apiConfig("acquistionAPI");
                      ajaxSettings.body = JSON.stringify(params);
                      self.ajaxCall(ajaxSettings,function(res,err){
                          self.hideLoading();
                          if(!res && err.lstEmailSignupErrorList==undefined){
                              console.log("Acquisition API Fails. " + err);
                              $parentElem.addClass("api-fails");
                              return;
                          }
                          var newRes = (err && err.lstEmailSignupErrorList!=undefined) ? err : res;
                          $parentElem.removeClass("api-fails");
                          if(newRes.lstEmailSignupErrorList.length){
                              var errorCode = "",
                                  errorMessage = "",
                                  firstItem;
                              _.each(newRes.lstEmailSignupErrorList,function(item,index){
                                  firstItem = !index ? true : false;
                                  self.displayAPIErrors(item.errorCode,item.errorDesc,firstItem,$parentElem);
                                  errorCode= errorCode=="" ? item.errorCode  : errorCode+'|'+item.errorCode;
                                  errorMessage= errorMessage=="" ?  item.errorDesc : errorMessage+'|'+item.errorDesc;
                              });
                              self.apiErrorForTracking(errorCode,errorMessage, params.Email);
                              return;
                          }
                          params.consumerId = newRes.consumerId; 
                          self.apiSuccessForTracking(params);
                          $(self.el).addClass("form-submitted");
                          if(elem.hasAttribute("data-action"))
                              window.location = $(elem).data("action");
                        //  else
                          //    alert("Thank you for participating in the contest!")
                      });
                  }
              });
          },
          apiSuccessForTracking : function(params){
              typeof apiSuccessForm == "function" && apiSuccessForm ({
                  customer_email_hash: typeof sha256 == "function" && sha256(params.Email),
                  customer_id: params.consumerId,
                  source_id: $("#formSourceId").val(),
                  subscription_ids: '117'


              })
          },
          apiErrorForTracking : function(errorCode,errorMessage,email){
              typeof apiFormError == "function" && apiFormError ({
                  error_code: errorCode,
                  error_message : errorMessage
              },typeof sha256 == "function" && sha256(email));
          },
          clientErrorForTracking : function(errorName,errorField,email){
              typeof clientFormValidationError == "function" && clientFormValidationError ({
                  error_name: errorName,
                  error_field : errorField
              },typeof sha256 == "function" && sha256(email));
          },
          formReset : function(ele,evt){
              var $removeAddedChild = $("#child-details-wrapper fieldset:not(.sub-fieldset):not(:first-child)");
              $("#child-details-wrapper fieldset:not(.sub-fieldset)").removeClass("child-data-saved");
              $removeAddedChild.length && $removeAddedChild.remove();
          },
          displayAPIErrors : function(errCode,errMsg, isFirstErr,parentElem){
              var apierrorcopy = this.apiMessages && this.apiMessages[errCode];
              switch(errCode){
                  case "1022":
                  case "1009":
                      this.errorAPIToggle($(".child-dob"),true, apierrorcopy);
                      if(isFirstErr) $(".child-dob input").focus();
                      break;
                  
                  case "1010":
                  case "1017":
                  case "1018":
                  case "1019":
                  case "1020":
                  case "1021":
                      $(parentElem).attr('data-content',apierrorcopy).addClass("api-fails");
                      break;
  
                  case "1004":
                  case "1029":
                      self.existEmail = $("#emailId").val();
                      this.errorAPIToggle($("#emailId"),true, apierrorcopy);
                      if(isFirstErr) $("#emailId").focus();
                  break;
              }
          },
          getClientToken : function(){
              /*var res = arguments[1];*/
              var self =this;
              this.ajaxCall(this.apiConfig("getToken"),function(res,err){
                  if(!res){
                      console.log("Invalid Client ID / Secret. " + err);
                      return;
                  }
                  self.clientTokenKey = res.token;
                  self.clientTokenExpires = {'expires' : res.tokenExpirationUtc }
                  cookie.prepend(self.tokenKeyName, self.clientTokenKey,self.clientTokenExpires)
              });
          },
          getDeviceType : function() {
              var isMobile = false; //initiate as false
              // device detection
              if (/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|ipad|iris|kindle|Android|Silk|lge |maemo|midp|mmp|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino/i.test(navigator.userAgent) || /1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(navigator.userAgent.substr(0, 4))) isMobile = true;
              return isMobile == true ? 'Mobile' : 'Desktop';
          },
          inputFocus : function(ele,evt){
            var code = evt.keyCode || evt.which;
            if (code != '9') { // not tab key
                CRMAEM.MRSForm.validateInput(ele);
            }
          },
          inputHover : function(ele,evt){
            let title= $(ele).attr("placeholder");  
            let formError = $(ele).parent().find(".form-error");
            let apiError = $(ele).parent().find(".api-error");
            if($(ele).val()!="") {
                title ="";
            }
            if(formError.is(":visible")) {
                title = $(formError).text();
            }
            if(apiError.is(":visible")) {
                title = $(apiError).text();
            }
            $(ele).attr("title", title);
          },
          radioFocus: function(ele,evt){
              var self = CRMAEM.MRSForm;
              var code = evt.keyCode || evt.which;
              if (code != '9') { // not tab key
                  CRMAEM.MRSForm.validateInput(ele);
              }
          },
          addRequiredAttr: function(inputEle){
              $(inputEle).attr('required',true);
              $(inputEle).parents('.form-group').addClass('required');
          },
          removeRequiredAttr: function(inputEle){
              $(inputEle).attr('required',false);
              $(inputEle).parents('.form-group').removeClass('required');
          },
          isEmail : function(email) {
              var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
              return regex.test(email);
          },
          isPhoneNumber : function(num) {
              var regex = /([0-9]{10})|(\([0-9]{3}\)\s+[0-9]{3}\-[0-9]{4})/;
              return regex.test(num);
          },
          isValidDate : function(dateString,format) {
              var m = dateString.match(/[0-9]{2}[/][0-9]{2}[/][0-9]{4}$/);
              return (!m) ? true : false;
          },
          isvalidZipCode : function(val){
              var regex = /^[A-Za-z0-9 ]*$/;
              return regex.test(val);
          },
          showLoading : function(ele){
              var $elem = ele == undefined ? $(this.el).parents(".form-component") : ele.parents('.form-group'),
                  loadingClassName = "data-loading";
              $elem.addClass(loadingClassName);
          },
          hideLoading : function(ele){
              var $elem = ele == undefined ? $(this.el).parents(".form-component") : ele.parents('.form-group'),
                  loadingClassName = "data-loading";
              $elem.removeClass(loadingClassName);
          },
          errorToggle : function(elm,isError) {
              var $inputParentElem = elm.parents('.form-group'),
                  hasReqAttribute = $(elm).attr('required'),
                  errorClassName = "has-error";
              if(isError){
                  $inputParentElem.addClass(errorClassName);
                  if (elm.val()=="" && typeof hasReqAttribute !== typeof undefined && hasReqAttribute !== false) {
                      $inputParentElem.addClass('has-required-error');
                  }
              } else{
                  $inputParentElem.removeClass(errorClassName).removeClass('has-api-error has-required-error');
              }
          },
          errorAPIToggle : function(elm,isError,msgCopy) {
              var $inputParentElem = elm.parents('.form-group'),
                  errorClassName = "has-api-error";
              if(isError){
                  if(msgCopy) $inputParentElem.find('.api-error').html(msgCopy);
                  $inputParentElem.addClass(errorClassName);
              } else{
                  $inputParentElem.removeClass(errorClassName);
              }
          },
          clearElmError : function(elm) {
              elm.parents('.form-group').removeClass('has-error');
          },
          validateInput : function(elm){
              var $elm = $(elm),                
                  inputType = $elm.attr('type'),
                  inputVal = $.trim($elm.val()),
                  ret = true;
              if (elm.hasAttribute('required') && inputType!="email") {
                  var error = false;
                  if(inputVal == ''){
                      error = true;
                      this.errorToggle($elm, error);
                      return;
                  }
                  if($elm.is(".child-dob input") && $elm.parents(".has-error").length){
                      error = true;
                      this.errorToggle($elm, error);
                      return;
                  }
                  this.errorToggle($elm, error);
              } 
              switch (inputType){
                  case "email":
                      this.errorToggle($elm,!this.isEmail(inputVal));
                      if(self.existEmail){
                        if(self.existEmail == inputVal){
                            this.errorAPIToggle($elm, true);
                        } else{
                            this.errorAPIToggle($elm, false);
                        }
                      } 
                      break;
  
                  case "tel":
                      this.errorToggle($elm,inputVal!="" && !this.isPhoneNumber(inputVal));
                      break;
  
                  case "text":
                      if(inputVal!= ''){
                          if($elm.is("#postalCode") && !this.isvalidZipCode(inputVal)){
                              this.errorToggle($elm, true);
                          } else if($elm.is(".child-dob input") && $elm.parents(".has-api-error").length){
                               this.errorAPIToggle($elm, false);
                          } 
                          else if(!elm.hasAttribute('required') && !$elm.is(".child-dob input")){
                              this.errorToggle($elm, false);
                          }
                      } else if($elm.is("#postalCode")){
                          this.errorToggle($elm, false);
                      }
                      break;
                  
                  default:
                      this.clearElmError($elm);
              }  
              return ret;
          },
          validateRadio : function(ele){
              var $elm = $(ele);
              this.errorToggle($elm,false);
              return true;
          },
          validateChildFields: function(){
              var self = CRMAEM.MRSForm;
              var $childGender = $(".child-gender input");
              var $childRelation = $(".child-relation input");
              var $childDOB = $(".child-dob input");
              if($childDOB.val()!="" && !$childGender.is(":checked") && !$childRelation.is(":checked")){
                  self.errorToggle($childGender, true);
                  console.log("gender or relationship to be filled..")
              } else if(($childGender.is(":checked") || $childRelation.is(":checked")) && $childDOB.val()==""){
                  console.log("DOB to be filled..")
                  self.errorToggle($childDOB, true);
              }
          },
          validateForm : function(elem,cb){
              var self = this;
              var $formEle =$(elem.form);
              var apiParams = {};
              var isAPIError = $formEle.find('.has-api-error').length;
              var isFormError;
              var childrensDetailsArr;
  
              if (isAPIError) {
                  $('html, body').animate({scrollTop: ($formEle.find('.has-api-error').last().offset().top)}, 'slow');
                  $formEle.find('.has-api-error').first().find('input').focus();
                  cb(false);
                  return;
              }
  
              _.each($formEle.find(".input_field"),function(inputEle){
                  self.validateInput(inputEle);
              });
              // prevent form sumbition if has error\
              isFormError = $formEle.find('.has-error').length;
              if (isFormError) {
                  $('html, body').animate({scrollTop: ($formEle.find('.has-error').first().offset().top)}, 'slow');
                  $formEle.find('.has-error').first().find('input').focus();
                  var errorName = "",
                      errorField = "",
                      $errorFieldElement,
                      $errorNameElememt;
  
                  $formEle.find('.has-error').filter(function(){
                      $errorNameElememt = $(this).find(".form-error").text();
                      $errorFieldElement = $(this).find(".input_field").attr("id");
                      errorName= errorName=="" ? $errorNameElememt  : errorName+'|'+$errorNameElememt;
                      errorField= errorField=="" ?  $errorFieldElement : errorField+'|'+$errorFieldElement;
                  })
                  self.clientErrorForTracking(errorName, errorField, $('#emailId').val());
                  cb(false);
                  return;
              }
              var stateVal = $('#stateField [aria-labelledby="stateDropdown"]').filter(':visible').val();
              var subscriptionId = document.getElementById("primarySubscriptionId").value;
              apiParams = {
                  "Email" : $('#emailId').val(),
                  "FirstName" : $('#parentFirstName').val(),
                  "LastName" : $('#parentLastName').val() || '',
                  "Country" : $("#countryDropdown").val() || '',
                  "State" : stateVal!="0" ? stateVal : '',
                  "City" : $("#cityField").val() || '',
                  "Address1" : $("#streetText").val() || '',
                  "Address2" : $("#streetText1").val() || '',
                  "ZipCode" : $("#postalCode").val() || '',
                  "CountryCode" : this.countryCode,
                  "Phone" : $('#contactNumber').val() || '',
                  "CustomFields" : [
                  {
                      "Key": "Comments",
                      "Value": $('#comment').val() || ''
                  }],
                  "Locale" : $("#formLocale").val() || '',
                  "Device" : this.getDeviceType(),
                  "SourceId" : this.sourceId,
				  "ListOfSubscriptionId" :subscriptionId.length ? subscriptionId : this.noSubscriptionId,

              };
              childrensDetailsArr = self.getChildDetailsParams();
              if(childrensDetailsArr.length){
                  apiParams.ListOfChildren = childrensDetailsArr
              }
              if($formEle[0].checkValidity()){
                  cb(apiParams)
              } else{
                  self.hideLoading();
              }
          },
          getChildDetailsParams : function(){
              var childrenArray = [];
              _.each($('#child-details-wrapper fieldset:not(.sub-fieldset)'),function(item){
                  var childrenSubObj = {};
                  _.each($(item).find(".child-data"), function(subItem){
                      var isoDate;
                      var datearray;
                      var date;
                      if(!_.isEmpty($(subItem).find("input[type='radio']:checked").val()) || !_.isEmpty($(subItem).find("input[type='number']").val()) || !_.isEmpty($(subItem).find("input[type='text']").val())){
                          if($(subItem).hasClass("child-gender")){
                              childrenSubObj.ChildGender = $(subItem).find("input[type='radio']:checked").val() || '';
                          } else if($(subItem).hasClass("child-relation")){
                              childrenSubObj.ChildRelationship = $(subItem).find("input[type='radio']:checked").val() || '';
                          } else if($(subItem).hasClass("child-dob")){
                              if($(subItem).find("input").val()!=''){
                                    datearray = $(subItem).data("date-format").toLowerCase()=="dd/mm/yyyy" ? $(subItem).find("input").val().split("/") : false;
                                    date = new Date(datearray &&  datearray[1] + '/' + datearray[0] + '/' + datearray[2] || $(subItem).find("input").val());
                                    isoDate = new Date(date.getTime() - (date.getTimezoneOffset() * 60000)).toISOString();
                                  childrenSubObj.ChildBirthdate = isoDate.split('T')[0];
                              } else{
                                  childrenSubObj.ChildBirthdate = '';
                              }
                          } else if($(subItem).hasClass("child-first-name")){
                              childrenSubObj.ChildFirstName = $(subItem).find("input[type='text']").val() || '';
                          }
                      }
                      
                  })
                  !_.isEmpty(childrenSubObj) && childrenArray.push(childrenSubObj)
              });
              return childrenArray;
          },
          bindDatePicker : function(hideFutureDate,triggerChange){
              var $el = hideFutureDate!=undefined ? $(".date-picker") : $(".date-picker:not('.active')"),
                  startYear = (new Date()).getFullYear() - this.belowChildAge || 14;
  
              if(!$el.length) return false;
              if(hideFutureDate!=undefined){
                  $el.datepicker('destroy');
              }
              var dateBinded = $el.datepicker({
                  startDate: new Date(startYear+'-01-01'), //set start date
                  autoclose: true,
                  forceParse: false
              }).addClass('active');
              if(triggerChange) dateBinded.trigger("change");
              return dateBinded;
          },
          changeStateDropdown : function(ele){
              var $el = $("#stateField"),
                  selectedVal = $(ele).val();
              $el.attr('data-country-selected',selectedVal);
              if($("#"+selectedVal+"-states").length){
                  $("#"+selectedVal+"-states option").prop('selected', function() {
                      return this.defaultSelected;
                  });
              } else{
                  $("#others-states").val('');
              }
  
          },
          loadStateDropdown : function(){
              this.changeStateDropdown($("#countryDropdown"))
          },
          loadAPIerrormessage : function(){
              var self = this;
              var ajaxSettings = self.apiConfig("getAPIErrorMessage");
              self.ajaxCall(ajaxSettings,function(res,err){
                  if(!res){
                      console.log("API error message data fails..error is "+ err);
                      return;
                  }
                  self.apiMessages = res;
              });
          },
          loadAxChanges : function(){
              var $targetTextAddedinHref = $('[data-href-target]');
              if(!$targetTextAddedinHref.length || !$targetTextAddedinHref.find("a[target='_blank']").length) return;
              $targetTextAddedinHref.find("a[target='_blank']").append("<span class='sr-only'>"+$targetTextAddedinHref.data('hrefTarget')+"</span>");
          },
          checkFormHasDataTracking : function(){
              var $form = $(this.el);
              var $inputField = $form.find("input[type='text'], input[type='email']").filter(function() { return $(this).val()!="" && $(this) });
              var obj = {};
              if($inputField.length){
                  obj.type = "browser close";
                  obj.last_accessed_field =  $inputField[$inputField.length-1].getAttribute("id") || $inputField[$inputField.length-1].getAttribute("name");
                  typeof checkFormHasData == "function" && checkFormHasData (obj);
                  // fn(obj);
                  return true;
              } else{
                  return false;
              }
          },
          getCountryCode : function(){
              var self = this;
              var cookieName = "country-code";
              var isValInCookie = cookie.get(cookieName);
              if(isValInCookie){
                  CRMAEM.geoCountryCode = isValInCookie;
                  this.preCheckedUS(this.geoCountryCode);
              } else{
                  var ajaxSettings = this.apiConfig("getGeoDetection");
                  var obj = {};
                  this.ajaxCall(ajaxSettings,function(res,err){
                      if(!res){
                          console.log("Geo Detection API fails..error is " + err)
                          return;
                      }
                      CRMAEM.geoCountryCode = JSON.parse(res).CountryCode;
                      obj.expires=self.cookieExpireDate;
                      cookie.prepend(cookieName, self.geoCountryCode,obj)
                      self.preCheckedUS(self.geoCountryCode);
                  })
              }
  
          },
          preCheckedUS : function(countryCode){
              var $checkboxElem = $("#agreeEmailNotifcation");
              if(countryCode=="US")
                  $checkboxElem.attr("checked",true);
          },
          customBindEvent : function(){
              var self = this;
              $('#parentFirstName, #parentLastName').on("keypress", function(key) {
                  if (key.charCode != 13 && (key.charCode < 97 || key.charCode > 122) && (key.charCode < 65 || key.charCode > 90) && (key.charCode != 45) && (key.charCode != 32) && (key.charCode != 0)  ) return false;
              });
              $('#contactNumber').on("keypress", function(event) {
                  var key = window.event ? event.keyCode : event.which;
                  var key_codes = [48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 0, 8, 13];
  
                  if (!($.inArray(key, key_codes) >= 0)) {
                     event.preventDefault();
                   }
              });
                $('#crm-form').on("click",".edit-child-option", function(event) {
                    var index = $('#crm-form .edit-child-option').index(this);
                    $("#child-details-wrapper .child-details-fieldset").eq(index).remove();
                    self.checkMaxChildren();
                    event.preventDefault();
                });
          },
          dobValidation : function(ele,curElem){
              var self = this,
                  $dobEle = $(ele.currentTarget).find('input'),
                  options = $(curElem).data('datepicker').o,
                  endDate = options.endDate,
                  datearray = options.format.toLowerCase()=="dd/mm/yyyy" ? $dobEle.val().split("/") : false,
                  newdate = datearray &&  datearray[1] + '/' + datearray[0] + '/' + datearray[2] || $dobEle.val(),
                  val = newdate,
                  isValidDate = self.isValidDate(val);
              // if(!isNaN(val) && val.length<4) return;
              if(val!="" && (String(new Date(val))=="Invalid Date") || (new Date(endDate) != "Invalid Date" && (+new Date(val) > +new Date(endDate)))){
                  self.errorToggle($dobEle, true);
                  $(curElem).datepicker('hide');
              } else if(val!="" && isValidDate){
                  self.errorToggle($dobEle, true);
                  $(curElem).datepicker('hide');
              }
              else{
                  self.errorToggle($dobEle, false);
              }
          },
          render: function(){
              var self  = this,
                  isTokenInCookie = cookie.get(this.tokenKeyName);
              if(isTokenInCookie){
                  this.clientTokenKey = isTokenInCookie;
              } else{
                  this.getClientToken();
              }
              this.loadStateDropdown();
              var datebinding = this.bindDatePicker(true);
              datebinding && datebinding
                  .keyup(function(ele){
                      self.dobValidation(ele,this)
                  }).change(function(ele){
                      self.dobValidation(ele,this)
                  });
              this.loadAPIerrormessage();
              this.loadAxChanges();
              this.getCountryCode();
              if($(this.el).find(".child-details-fieldset").length) {
                if($(this.el).find(".child-details-fieldset .child-dob").parents(".form-group").hasClass("required")) {
                    $(this.el).find(".child-details-fieldset .child-dob").parents(".form-group").removeClass("required");
                    $(this.el).find(".child-details-fieldset .child-dob input").removeAttr("required");
                    this.dobRequired = true;
                }
              }
              this.maxChildren = parseInt($(this.el).find("#child-details-wrapper").attr("data-max-children"));              
          },
          init: function () {
              if (!CRMAEM.isDependencyLoaded || !$(this.el).length || CRMAEM.form) return;
              CRMAEM.bindLooping(this.bindingEventsConfig(), this);
              this.customBindEvent();
              this.render();
          }
      }
      CRMAEM.MRSForm = form;
      form.init();
      document.addEventListener('DOMContentLoaded', function () {
          if (!CRMAEM.isDependencyLoaded) {
              form.init();
          }
      }, false);
      window.addEventListener("beforeunload", function (e) {
        if(!$(CRMAEM.MRSForm.el).hasClass("form-submitted") && CRMAEM.MRSForm.checkFormHasDataTracking()){
            var confirmationMessage = "\o/";
  
            (e || window.event).returnValue = confirmationMessage; 
            return confirmationMessage;  
        }
  
      });
  
  }(window, CRMAEM, CRMAEM.cookie));
