<%--
userguide.css.dsp

{{IS_NOTE
	Purpose:

	Description:

	History:
		Thu Jul 1 11:27:21     2009, Created by kindalu
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
${z:setCSSCacheControl()}

html {overflow:hidden;}
img { -ms-interpolation-mode:bicubic }
body {
	padding: 0 !important;
        
}

.z-window, .z-window-highlighted, .z-window-highlighted-shadow{
        position: relative;
        width: 60%;
        overflow: auto;
}

.z-cell {
        padding : 5px 5px 5px 5px;
}

.header {
        font-weight: bold;
}

.z-panel-hm .z-panel-header, .z-panel-header{
font-weight: bolder; font-size: 14px;
}

<!--Panel Style-->
div.z-panel{
margin-bottom: 10px;
}

<!--Listbox Style-->
.listboxheader .z-label{
font-weight: bold;
}
.listboxcontent .z-label{
}

.content .z-label .z-cell{
    text-align: right;
}

div.z-listheader-cnt{
padding: 2px;
}
div.z-listheader-cnt .z-label {
padding: 2px;
font-weight: bold;
}
