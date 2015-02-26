jQuery.fn.formatCurrency = function(){
		$(this).autoNumeric({
			aSep : '.',
			aDec : ',',
			vMax : '2147483647.99',
			aSign : 'R$ '
		});
}

jQuery.fn.formatCurrencyDolar = function(){
	$(this).autoNumeric({
		aSep : '.',
		aDec : ',',
		vMax : '2147483647.99',
		aSign : 'U$ '
	});
}