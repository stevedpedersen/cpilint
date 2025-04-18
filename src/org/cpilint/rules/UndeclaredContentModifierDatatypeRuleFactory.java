package org.cpilint.rules;

import java.util.List;
import java.util.stream.Collectors;

import org.dom4j.Element;

public final class UndeclaredContentModifierDatatypeRuleFactory implements RuleFactory {

	@Override
	public boolean canCreateFrom(Element e) {
		return e.getName().equals("undeclared-content-modifier-datatype-not-allowed");  
	}

	@Override
	public Rule createFrom(Element e) {
		if (!canCreateFrom(e)) {
			throw new RuleFactoryError(String.format("Cannot create Rule object from element '%s'", e.getName()));
		}
		return new UndeclaredContentModifierDatatypeRule();
	}

}