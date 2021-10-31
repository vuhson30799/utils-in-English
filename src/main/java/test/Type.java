package test;

public enum Type {
	ADV("(adv)"),
	ADJ_DOT("(adj.)"),
	ADJ("(adj)"),
	A("(a)"),

	N_DOT("(n.)"),
	N_DOT_PHR_DOT("(n. phr.)"),
	N("(n)"),

	V("(v)"),
	V_DOT("(v.)"),
	V_DOT_PHR_DOT("(v. phr.)"),
	PHR_DOT_V_DOT("(phr. v.)"),

	N_AND_V("(n,v)"),
	V_AND_N("(v,n)"),
	IDIOM("(idiom)"),
	COMPOUND_N("(compound n.)");

	String pattern;

	Type(String s) {
		this.pattern = s;
	}
}
