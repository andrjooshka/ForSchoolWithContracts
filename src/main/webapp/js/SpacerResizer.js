$j(document).ready(function() {
	totalWidth = $j(document).width();
	spacerOne = $j(".spacer.first");
	spacerTwo = $j(".spacer.middle");
	spacerThree = $j(".spacer.last");
	availWidth = totalWidth - spacerOne.width() - spacerTwo.width();
	if (availWidth < 0)
		availWidth = 0;
	spacerThree.css("width", availWidth + "px");
});