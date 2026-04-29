package com.commander4j.network;


import java.util.LinkedList;

public class ControlCodes
{
	private LinkedList<ControlCode> codelist = new LinkedList<ControlCode>();
	
	
	public  LinkedList<ControlCode> getControlCodes()
	{
		return codelist;
	}
	
	public ControlCodes()
	{
		codelist.addLast(new ControlCode("<NUL>","\u0000" ));
		
		codelist.addLast(new ControlCode("<SOH>","\u0001" ));
		
		codelist.addLast(new ControlCode("<STX>","\u0002" ));
		
		codelist.addLast(new ControlCode("<ETX>","\u0003" ));
		
		codelist.addLast(new ControlCode("<EOT>","\u0004" ));
		
		codelist.addLast(new ControlCode("<ENQ>","\u0005" ));
		
		codelist.addLast(new ControlCode("<ACK>","\u0006" ));
		
		codelist.addLast(new ControlCode("<BEL>","\u0007" ));
		
		codelist.addLast(new ControlCode("<BS>","\u0008" ));
		
		codelist.addLast(new ControlCode("<HT>","\u0009" ));
		
		codelist.addLast(new ControlCode("<TAB>","\u0009" ));
		
		codelist.addLast(new ControlCode("<LF>","\n" ));
		
		codelist.addLast(new ControlCode("<VT>","\u000B" ));
		
		codelist.addLast(new ControlCode("<FF>","\u000C" ));
		
		codelist.addLast(new ControlCode("<CR>","\r" ));
		
		codelist.addLast(new ControlCode("<SO>","\u000E" ));
		
		codelist.addLast(new ControlCode("<SI>","\u000F" ));
		
		codelist.addLast(new ControlCode("<DLE>","\u0010" ));
		
		codelist.addLast(new ControlCode("<DC1>","\u0011" ));
		
		codelist.addLast(new ControlCode("<DC2>","\u0012" ));
		
		codelist.addLast(new ControlCode("<DC3>","\u0013" ));
		
		codelist.addLast(new ControlCode("<DC4>","\u0014" ));
		
		codelist.addLast(new ControlCode("<NAK>","\u0015" ));
		
		codelist.addLast(new ControlCode("<SYN>","\u0016" ));
		
		codelist.addLast(new ControlCode("<ETB>","\u0017" ));
		
		codelist.addLast(new ControlCode("<CAN>","\u0018" ));
		
		codelist.addLast(new ControlCode("<EM>","\u0019" ));
		
		codelist.addLast(new ControlCode("<SUB>","\u001A" ));
		
		codelist.addLast(new ControlCode("<ESC>","\u001B" ));
		
		codelist.addLast(new ControlCode("<FS>","\u001C" ));
		
		codelist.addLast(new ControlCode("<GS>","\u001D" ));
		
		codelist.addLast(new ControlCode("<RS>","\u001E" ));
		
		codelist.addLast(new ControlCode("<US>","\u001F" ));
		
		codelist.addLast(new ControlCode("<DEL>","\u007F" ));	
		
	}
}
