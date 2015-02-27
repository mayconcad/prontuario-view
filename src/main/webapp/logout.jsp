<%
	HttpSession s = request.getSession(false);
	if (s != null) {
		s.invalidate();
	}

	for (Cookie c : request.getCookies()) {
		c.setMaxAge(0);
	}
	String appContext = request.getContextPath();
	if (appContext.equals("")) {
		appContext = "/";
	} else {
		appContext += "/";
	}
	response.sendRedirect(appContext);
%>