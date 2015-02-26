$(document).ready(function() {

	setTimeout(function() {

		$("div.ui-selectonemenu").each(function() {
			$(this).css("width", "");
		});

		$("label.ui-selectonemenu-label").each(function() {
			$(this).css("width", "97%");
		});
		
	});

});

