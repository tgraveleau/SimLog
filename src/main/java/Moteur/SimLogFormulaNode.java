package Moteur;

public class SimLogFormulaNode {

	private SimLogFormulaNode left, right, parent;
	private int operator;
	private String variable;
	private int depth = 0;

	public SimLogFormulaNode(int oper, SimLogFormulaNode l, SimLogFormulaNode r) {
		parent = null;
		left = l;
		right = r;
		if (l != null)
			l.setParent(this);
		if (r != null)
			r.setParent(this);
		operator = oper;
		variable = null;
	}

	public SimLogFormulaNode(String s, SimLogFormulaNode l, SimLogFormulaNode r) {
		parent = null;
		left = l;
		right = r;
		if (l != null)
			l.setParent(this);
		if (r != null)
			r.setParent(this);
		operator = 0;
		variable = s;
	}

	public void setParent(SimLogFormulaNode n) {
		parent = n;
	}

	public SimLogFormulaNode getParent() {
		return parent;
	}

	public SimLogFormulaNode getLeft() {
		return left;
	}

	public SimLogFormulaNode getRight() {
		return right;
	}

	public int getType() {
		return operator;
	}

	public String getVariable() {
		return variable;
	}

	public boolean isLeaf() {
		if ((left == null) && (right == null))
			return true;
		return false;
	}
}