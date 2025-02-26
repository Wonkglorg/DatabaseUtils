package com.wonkglorg.database.jdbi;

import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.argument.ArgumentFactory;
import org.jdbi.v3.core.config.ConfigRegistry;

import java.lang.reflect.Type;
import java.util.Optional;

public class PathArgumentFactory implements ArgumentFactory{
	
	@Override
	public Optional<Argument> build(Type type, Object value, ConfigRegistry config) {
		return Optional.empty();
	}
}
