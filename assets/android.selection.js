// Namespace
var android = {};
android.selection = {};
android.editFlage = false;
android.selection.selectionStartRange = null;
android.selection.selectionEndRange = null;

/** The last point touched by the user. { 'x': xPoint, 'y': yPoint } */
android.selection.startTouchPoint = null;

/**
 * Starts the touch and saves the given x and y coordinates as last touch point
 */
android.selection.startTouch = function(x, y) {

	android.selection.startTouchPoint = {
		'x' : x,
		'y' : y
	};

};

/**
 * Checks to see if there is a selection.
 * 
 * @return boolean
 */
android.selection.hasSelection = function() {
	return window.getSelection().toString().length > 0;
};

/**
 * Clears the current selection.
 */
android.selection.clearSelection = function() {

	try {
		// if current selection clear it.
		var sel = window.getSelection();
		sel.removeAllRanges();
	} catch (err) {
		window.TextSelection.jsError(err);
	}
};

android.selection.getElementLeft = function(element){
　　　　var actualLeft = element.offsetLeft;
　　　　var current = element.offsetParent;
　　　　while (current !== null){
　　　　　　actualLeft += current.offsetLeft;
　　　　　　current = current.offsetParent;
　　　　}
　　　　return actualLeft;
};

android.selection.getElementTop = function (element){
　　　　var actualTop = element.offsetTop;
　　　　var current = element.offsetParent;
　　　　while (current !== null){
　　　　　　actualTop += current.offsetTop;
　　　　　　current = current.offsetParent;
　　　　}
　　　　return actualTop;
};

/**
 * Handles the long touch action by selecting the last touched element.
 */
android.selection.longTouch = function() {
	
//	var content_p = document.getElementById("content_p");
//	var rect = content_p.getBoundingClientRect();
	// console.log('=======selection
	// start========='+android.selection.startTouchPoint.x);
//	console.log('=======绝对位置Top:===='+android.selection.getElementTop(content_p));
//	console.log('=======绝对位置Left:===='+android.selection.getElementLeft(content_p));
//	
//	console.log('=======content_p.offsetTop:'+content_p.offsetTop+'=======content_p.offsetLeft:'+content_p.offsetLeft+'=======content_p.offsetBottom:'+(content_p.offsetTop+content_p.offsetHeight)+'=======content_p.offsetRight:'+(content_p.offsetLeft+content_p.offsetWidth));
//	console.log('=======content_p.top:'+rect.top+'=======content_p.left:'+rect.left+'=======content_p.bottom:'+rect.bottom+'=======content_p.right:'+rect.right);
	android.editFlage = true;
	try {
		android.selection.clearSelection();
		
		
		var sel = window.getSelection();
		var oneWordCaret = document.caretRangeFromPoint(android.selection.startTouchPoint.x,android.selection.startTouchPoint.y);
		oneWordCaret.expand("word");

		android.selection.selectionStartRange = oneWordCaret;

		
		
		sel.addRange(oneWordCaret);

	} catch (err) {
		console.log('=======err========='+err);
	}

};


android.selection.endTouch = function(){
	var sel = window.getSelection();
	sel.removeAllRanges();
	
	var selectionEnd = $("<a id=\"selectionEnd\"></a>");
	var selectionStart = $("<a id=\"selectionStart\"></a>");
	android.selection.selectionStartRange.insertNode(selectionStart[0]);
	android.selection.selectionEndRange.insertNode(selectionEnd[0]);
	var newNode = document.createElement("span");
	
	
	var sNode = document.getElementById("selectionStart");
	var endNode = document.getElementById("selectionEnd");
	

	var range = document.createRange();
	range.setStartAfter(sNode);
	range.setEndBefore(endNode);
	
	

	
	var documentFragment = range.extractContents();
	newNode.appendChild(documentFragment);
	
	//range.surroundContents(newNode);
	endNode.parentNode.insertBefore(newNode, endNode);
	
	if (sNode) {
		sNode.parentNode.removeChild(sNode);
	}
	
	if (endNode) {
		endNode.parentNode.removeChild(endNode);
	}
}

/**
 * Starts the touch and saves the given x and y coordinates as last touch point
 */
android.selection.moveTouch = function(x, y) {
	var endNode = document.getElementById("selectionEnd");
	if (endNode) {
		endNode.parentNode.removeChild(endNode);
	}

	android.selection.selectionEndRange = document.caretRangeFromPoint(x, y);

// var selectionEnd = $("<a id=\"selectionEnd\"></a>");
// android.selection.selectionEndRange.insertNode(selectionEnd[0]);


// var sNode = document.getElementById("selectionStart");
// endNode = document.getElementById("selectionEnd");
	
	var sel = window.getSelection();
	
// range.setStartAfter(sNode);
// range.setEndBefore(endNode);
	
	
	
	if(sel.rangeCount == 0){
		var range = document.createRange();
		range.setStart(android.selection.selectionStartRange.startContainer,android.selection.selectionStartRange.startOffset);
		range.setEnd(android.selection.selectionEndRange.startContainer,android.selection.selectionEndRange.startOffset);
		//sel.removeAllRanges();
		sel.addRange(range);
	}else{
		var rangelast = sel.getRangeAt(sel.rangeCount-1);
		
		var lastrect = rangelast.getBoundingClientRect();
	
		if(lastrect&&(lastrect.right)>(x+50) && (lastrect.bottom)>(y+50)){
		
			sel.removeAllRanges();
			var range = document.createRange();
			range.setStart(android.selection.selectionStartRange.startContainer,android.selection.selectionStartRange.startOffset);
			range.setEnd(android.selection.selectionEndRange.startContainer,android.selection.selectionEndRange.startOffset);
			sel.addRange(range);
		}else{
			var range = document.createRange();
			range.setStart(rangelast.endContainer,rangelast.endOffset);
			range.setEnd(android.selection.selectionEndRange.startContainer,android.selection.selectionEndRange.startOffset);
			//sel.removeAllRanges();
			sel.addRange(range);
		}
		
		//sel.removeRange(sel.getRangeAt(sel.rangeCount-2));
	}
	
	
	
//	
// if(sel.rangeCount == 0){
// var range = document.createRange();
// range.setStartAfter(sNode);
// range.setEndBefore(endNode);
//		
// sel.removeAllRanges();
// sel.addRange(range);
// }else if(sel.rangeCount == 1){
// sel.removeRange(sel.getRangeAt(sel.rangeCount-1))
// var rangelast = document.createRange();
// var range = document.createRange();
// range.setStart(rangelast.endContainer,rangelast.endOffset);
// range.setEndBefore(endNode);
// sel.addRange(range);
// }else{
// sel.removeRange(sel.getRangeAt(sel.rangeCount-1))
// var rangelast = sel.getRangeAt(sel.rangeCount-1);
// var range = document.createRange();
// range.setStart(rangelast.endContainer,rangelast.endOffset);
// range.setEndBefore(endNode);
// sel.addRange(range);
// }
	
//	
// var range = document.createRange();
// range.setStartAfter(sNode);
// range.setEndBefore(endNode);
	

//	
	
// documentFragment = range.extractContents();
//	
// console.log('=======documentFragment========'+documentFragment);
//	
// var innerSpan = document.createElement("span");
// innerSpan.appendChild(documentFragment);
// documentFragment=null;
// //android.selection.insertAfter(innerSpan, sNode);
// endNode.parentNode.insertBefore(innerSpan, endNode);

};

// android.selection.insertAfter = function(newElement, targetElement) {
// var parent = targetElement.parentNode; // 把目标元素的parentNode属性值提到到变量parent里
// if (parent.lastChild == targetElement) { //
// 检查目标元素是不是parent的最后一个元素，如果是，则直接追加到目标元素后面
// parent.appendChild(newElement);
// } else {
// parent.insertBefore(newElement, targetElement, targetElement.nextSibling); //
// 如果不是，则把新元素插入到目标元素和parent元素的下一个子元素的中间
// }
// }
