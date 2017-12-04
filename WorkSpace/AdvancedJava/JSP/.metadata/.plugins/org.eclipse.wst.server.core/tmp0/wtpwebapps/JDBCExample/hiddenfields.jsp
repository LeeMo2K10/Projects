<%@ page session="true"%>
<H3>Number Guess Guesser</H3>
<%!public static final int WAY_LO = 0;
	public static final int WAY_HI = 101;
	public static final String PARMSKEY = "jspcr.sessions.numguess.parameters";

	// Inner class containing state variables
	public class Parameters {
		int lo;
		int hi;
		int numGuesses;
		int state;
	}%>
<%
	Parameters parms = (Parameters) session.getAttribute(PARMSKEY);
	if (parms == null) {
		parms = new Parameters();
		parms.state = 0;
		session.setAttribute(PARMSKEY, parms);
	}
	switch (parms.state) {
	case 0: {
		// Initial screen
%>
<FORM>
	Think of a number between
	<%=WAY_LO + 1%>
	and
	<%=WAY_HI - 1%>, and I'll try to guess it.
	<P>Click OK when ready.
	<P>
		<INPUT TYPE="submit" VALUE="OK">
</FORM>
<%
	parms.lo = WAY_LO;
		parms.hi = WAY_HI;
		parms.numGuesses = 0;
		parms.state = 1;
		break;
	}
	case 1: {
		// First guess
		parms.numGuesses++;
		int guess = (parms.hi + parms.lo) / 2;
%>
<FORM>
	My first guess is
	<%=guess%>. How did I do?
	<P>
		<INPUT TYPE="radio" NAME="result" VALUE="-1" onClick="submit()">
		Too low <INPUT TYPE="radio" NAME="result" VALUE="0" onClick="submit()">
		Exactly right <INPUT TYPE="radio" NAME="result" VALUE="1"
			onClick="submit()"> Too high
</FORM>
<P>
	<%
		parms.state = 2;
			break;
		}
		case 2: {
			// After first guess
			parms.numGuesses++;
			int result = Integer.parseInt(request.getParameter("result"));
			int guess = (parms.hi + parms.lo) / 2;
			if (result < 0) {
				parms.lo = guess;
				guess = (parms.hi + parms.lo) / 2;
			} else if (result > 0) {
				parms.hi = guess;
				guess = (parms.hi + parms.lo) / 2;
			}
			if (result != 0) {
	%>

<FORM>
	<%
		if (parms.lo > WAY_LO)
					out.println(parms.lo + " is too low.<BR>");
				if (parms.hi < WAY_HI)
					out.println(parms.hi + " is too high.<BR>");
				if ((parms.hi - parms.lo) > 1) {
	%>
	My next guess is
	<%=guess%>. How did I do?
	<P>
		<INPUT TYPE="radio" NAME="result" VALUE="-1" onClick="submit()">
		Too low <INPUT TYPE="radio" NAME="result" VALUE="0" onClick="submit()">
		Exactly right <INPUT TYPE="radio" NAME="result" VALUE="1"
			onClick="submit()"> Too high
</FORM>
<%
	} else {
				String[] text = { "Are we cheating?", "Did we forget our number?",
						"Perhaps we clicked the wrong button?", "What happened?", "What gives?", };
				String message = text[(int) (Math.random() * text.length)];
				session.removeAttribute(PARMSKEY);
%>
<FORM>
	<%=message%><P>
		<INPUT TYPE="SUBMIT" VALUE="Start Over">
</FORM>
<%
	}
		} else {
			parms.numGuesses--;
%>
<FORM>
	I win, and after only
	<%=parms.numGuesses%>
	guesses!
	<P>Do you want to try again?
	<P>
		<INPUT TYPE="SUBMIT" VALUE="Start Over">
</FORM>
<%
	session.removeAttribute(PARMSKEY);
		}
		break;
	}
	}
%>