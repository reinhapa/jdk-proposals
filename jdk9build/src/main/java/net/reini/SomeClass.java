package net.reini;

import net.reini.internal.SomeInternalClass;

public class SomeClass {
	private SomeInternalClass internalClass;

	public SomeClass() {
		internalClass = new SomeInternalClass();
	}

	@Override
	public String toString() {
		return "SomeClass with: " + internalClass;
	}
}