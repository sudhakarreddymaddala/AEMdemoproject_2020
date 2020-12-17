/**
 * Country Drop Down.js
 * Version 1.0
 * applied in gallery components like image, character, products..,
 */

(function (global, PLAYAEM) {
    var country_dropdown = {
        el: '#fp-countryDropdown',        
        selectCountry: function (ele, eve) {
            var lochref = $(this.el).find(":selected").val();
            var countryName = $(this.el).find(":selected").text();
            populateCountryDropDownData(countryName);
            var targetValue = $(".country-drop-down").attr('value');
            if(targetValue == '_self'){
            	window.open("//" + lochref,'_self');
            }
            else if(targetValue == '_blank') {
            	window.open("//" + lochref,'_blank');
            }
            },
        init: function () {
            var self = this;
            if (!PLAYAEM.isDependencyLoaded || !$(this.el).length || PLAYAEM.country_dropdown) return;            
            setTimeout(function(){
                $(".country-drop-down div[role='listbox']").attr({"aria-selected":"true","aria-label": "country dropdown" });
                $(".country-drop-down button[data-id='countryDropdown']").removeAttr("role");
                $(".country-drop-down div[role='combobox']").attr({"aria-expanded":"false", "aria-controls":"countryComboBootstrap"});
                $("ul.dropdown-menu").attr("id","countryComboBootstrap");
            },5000);
            $('#fp-countryDropdown').on('hidden.bs.select', function (e) {                
                $(".country-drop-down div[role='combobox']").attr("aria-expanded","false");
            });
            $('#fp-countryDropdown').on('shown.bs.select', function (e) {      
                $('.popover_window').hide();          
                $(".country-drop-down div[role='combobox']").attr("aria-expanded","true");
            });
            $('.country-drop-down').on('click', '.dropdown-menu li a', function (e) {                
                self.selectCountry();
            });
            var slashSepURL = window.location.pathname.split('/');
			var url = window.location.host + '/' + slashSepURL[1];
			$(".country-drop-down .selectpicker").selectpicker('val', url); 
        }
    }
    country_dropdown.init();
    PLAYAEM.country_dropdown = country_dropdown;
    document.addEventListener('DOMContentLoaded', function () {
        if (!PLAYAEM.isDependencyLoaded) {
            country_dropdown.init();
        }
    }, false);
}(window, PLAYAEM));
