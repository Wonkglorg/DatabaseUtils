package com.wonkglorg.database.datasources;

import java.sql.Connection;

public class PooledConnection extends UncloseAbleConnection{
	private final PooledServerDataSource pool;
	private boolean returned = false;
	
	public PooledConnection(Connection delegate, PooledServerDataSource pool) {
		super(delegate);
		this.pool = pool;
	}
	
	@Override
	public void close() {
		if(!returned){
			returned = true;
			pool.releaseConnection(super.delegate);
		}
	}
}