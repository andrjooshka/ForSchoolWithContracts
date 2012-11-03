Event.observe(window, "resize", ResizeSpace);

function ResizeSpace() {
	totalWidth = $('mainmenu').getWidth();

	spacerOne = $('spacerOne');
	spacerTwo = $('spacerTwo');
	spacerThree = $('spacerThree');
	availWidth = totalWidth - spacerOne.getWidth() - spacerTwo.getWidth() - 1;
	if (availWidth < 0)
		availWidth = 0;
	style = {
		width : new String(availWidth+'px')
	};
	spacerThree.setStyle(style);
}