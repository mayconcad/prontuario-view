$(function() {
	$('.currency').maskMoney({
		symbol : "R$ ",
		showSymbol : true,
		symbolStay : true,
		decimal : ",",
		thousands : ".",
		defaultZero : false
	});

	$(".currency").attr("maxlength", 18);
});

$(document).ready(
		function($) {

			$(".currency").attr("maxlength", 18);
			$(".currency").keypress(isLetra);
			$(".currency").keyup(function() {
				$(this).val($(this).val().toUpperCase());
			});

			function isNumber(e) {
				if (e.which != 8 && e.which != 0
						&& (e.which < 48 || e.which > 57)) {
					return false;
				}
			}

			function isLetra(e) {
				if (e.which != 8 && e.which != 0
						&& !(em(e.which, 65, 90) || em(e.which, 97, 122))) {
					return false;
				}
			}

			function em(number, min, max) {
				if (number >= min && number <= max)
					return true;
				else
					return false;
			}

		});
