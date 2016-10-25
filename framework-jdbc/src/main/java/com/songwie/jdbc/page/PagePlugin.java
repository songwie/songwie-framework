package com.songwie.jdbc.page;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcUtils;
import com.songwie.util.aspect.Request;
import com.songwie.util.ip.IPUtil;
 
/**
 * @Description: 分页插件
 * @author songwei 
 * @date 2016-10-8
 */
@Intercepts({
	@Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class,Integer.class }),
	@Signature(type = StatementHandler.class, method = "update", args = { Statement.class }),
	@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
	@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = { Statement.class })
})
@SuppressWarnings("rawtypes")
@Service
public class PagePlugin implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(PagePlugin.class);

	public static final String PAGE_KEY = "page";
	public static Map<String, String> tables = new HashMap<String, String>();

	private static final ThreadLocal<Page> PAGE_CONTEXT = new ThreadLocal<Page>();
	private static String dialect = "mysql"; // 数据库方言
	private static String pageSqlId = ".*selectListPage.*"; // mapper.xml中需要拦截的ID(正则匹配)

	//private ThreadLocal<Transaction> sqlLocal = new ThreadLocal<Transaction>();
	
	/**
	 * @Description: 拦截处理
	 * @author alex (90167)
	 * @date 2014-1-10 下午4:02:09
	 */
	public Object intercept(Invocation ivk) throws Throwable {
        String methodName = ivk.getMethod().getName();

        if(methodName.equals("prepare")){
    		this.prepare(ivk);
        }else if(methodName.equals("update")){
        	 
        }else if(methodName.equals("query")){
        	Executor executorProxy = (Executor) ivk.getTarget();
            return query(ivk,executorProxy,ivk.getArgs());
        }else if(methodName.equals("handleResultSets")){
        	return this.catLogEnd(ivk);
        }
        return ivk.proceed();
	}
 
	public Object prepare(Invocation ivk) throws Exception {
        String operationSource = "";
        
		if (ivk.getTarget() instanceof RoutingStatementHandler) {
			RoutingStatementHandler statementHandler = (RoutingStatementHandler) ivk.getTarget();
			BaseStatementHandler delegate = (BaseStatementHandler) ReflectHelper.getValueByFieldName(statementHandler, "delegate");
			MappedStatement mappedStatement = (MappedStatement) ReflectHelper.getValueByFieldName(delegate, "mappedStatement");

			if (mappedStatement.getId().matches(pageSqlId)) { // 拦截需要分页的SQL
				BoundSql boundSql = delegate.getBoundSql();

				Object parameterObject = boundSql.getParameterObject();// 分页SQL<select>中parameterType属性对应的实体参数，即Mapper接口中执行分页方法的参数,该参数不得为空
				if (parameterObject == null) {
					throw new NullPointerException("parameterObject尚未实例化！");
				} else {
    					BoundSql countBS = delegate.getBoundSql();
    					String sql = countBS.getSql();
    					if (parameterObject instanceof HashMap) {
    						HashMap map = (HashMap) parameterObject;
    						Page p = (Page) map.get(PAGE_KEY);
    						if (p != null) {
    							p.setTotalResult(queryTotal(ivk, mappedStatement, countBS, parameterObject, sql));
    							set(p);
    							ReflectHelper.setValueByFieldName(countBS, "sql", pageSql(sql, p)); // 将分页sql语句反射回BoundSql.
    						}

    					}
				}
			} 
			return this.catLog(ivk, delegate, operationSource,mappedStatement);
		}
		
		return ivk.proceed();
    }
	
	private void changeSql(BaseStatementHandler delegate) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		if(Request.getId()!=null && !Request.getId().equals("")){
			BoundSql boundSql = delegate.getBoundSql();
			String sql = boundSql.getSql();
			sql = "/*requestId:"+ Request.getId() +",ip:"+ IPUtil.getLocalIp() +",threadId:"+ Thread.currentThread().getName() +"*/" + sql ;
			ReflectHelper.setValueByFieldName(boundSql, "sql", sql); // 将分页sql语句反射回BoundSql.
		}
	}
	public Object query(Invocation ivk,Executor executor, Object[] args) throws Exception {
        MappedStatement ms = (MappedStatement) args[0];
        if (ms.getId().matches(pageSqlId)) { //
        	executor.clearLocalCache();
        }
        return ivk.proceed();
    }
	
	private Object catLogEnd(Invocation ivk) throws InvocationTargetException, IllegalAccessException{
		Object returnObj = null;
		try {
    		returnObj = ivk.proceed();
    		//Transaction t = sqlLocal.get();
            //t.setStatus(Transaction.SUCCESS);
            //t.complete();  
		} catch (Exception ex) {
			logger.error("cat监控:" + ex.getMessage());
		}finally{
			if(returnObj==null){
				returnObj = ivk.proceed();
			}
		}
		return returnObj;
	}
	
	private Object catLog(Invocation ivk,BaseStatementHandler delegate,String operationSource, MappedStatement ms) throws InvocationTargetException, IllegalAccessException, SecurityException, NoSuchFieldException, IllegalArgumentException{
		this.changeSql(delegate);
		
		Object returnObj = null;
		//cat监控
		boolean success = false;
		try {
			//Transaction t = Cat.newTransaction("SQL", Request.getId() +"-"+ operationSource);
			try { 
				BoundSql countBS = delegate.getBoundSql();
				Object param = countBS.getParameterObject();// 分页SQL<select>中parameterType属性对应的实体参数，即Mapper接口中执行分页方法的参数,该参数不得为空

				String sql = countBS.getSql();
				BoundSql bs = new BoundSql(ms.getConfiguration(), sql, countBS.getParameterMappings(), param);
				HashMap<String, String> params = setParametersNew(ms, bs, param);
				sql = bs.getSql();
				sql = sql.replaceAll("\r|\n", " ");
				sql = sql.replaceAll("  ", " ");
				
				String method  = "";
				boolean select = true;
				 
				logger.info("SQL.Method"+ Request.getId() +"-"+ method);
				if(!select){
					logger.info("SQL.Statement "+ Request.getId() +"-"+ sql);
					logger.info("SQL.Statement "+ Request.getId() +"-"+ params);
				}

			    returnObj = ivk.proceed();
			    success = true;
			    //sqlLocal.set(t);
			} catch(Exception e) {
				logger.warn("catLog warn " + e.getMessage());
			   //Cat.logError(e);
			} 
		} catch (Exception ex) {
			logger.error("cat监控:" + ex.getMessage());
		}finally {
			if(returnObj==null && success==false){
				returnObj = ivk.proceed();
			}
		}
		return returnObj;
	}
	
	/**
	 * 对SQL参数(?)设值,参考org.apache.ibatis.executor.parameter.
	 * DefaultParameterHandler
	 * 
	 * @param ps
	 * @param mappedStatement
	 * @param boundSql
	 * @param parameterObject
	 * @throws SQLException
	 * @author alex (90167)
	 */
	@SuppressWarnings("unchecked")
	private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql,
			Object parameterObject, HashMap<String, String> map) throws SQLException {
		ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());

		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		if (parameterMappings != null) {
			Configuration configuration = mappedStatement.getConfiguration();
			TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
 
			for (int i = 0; i < parameterMappings.size(); i++) {
				ParameterMapping parameterMapping = parameterMappings.get(i);

				if (parameterMapping.getMode() != ParameterMode.OUT) {
					Object value;
					String propertyName = parameterMapping.getProperty();

					PropertyTokenizer prop = new PropertyTokenizer(propertyName);
					if (parameterObject == null) {
						value = null;
					} else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
						value = parameterObject;
					} else if (boundSql.hasAdditionalParameter(propertyName)) {
						value = boundSql.getAdditionalParameter(propertyName);
					} else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX)
							&& boundSql.hasAdditionalParameter(prop.getName())) {
						value = boundSql.getAdditionalParameter(prop.getName());
						if (value != null) {
							value = configuration.newMetaObject(value)
									.getValue(propertyName.substring(prop.getName().length()));
						}
					} else {
						value = metaObject == null ? null : metaObject.getValue(propertyName);
					}
					TypeHandler typeHandler = parameterMapping.getTypeHandler();
					if (typeHandler == null) {
						throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName
								+ " of statement " + mappedStatement.getId());
					}

					if (map != null) {
						map.put(propertyName, value != null ? value.toString() : "null");
					}

					if (ps != null) {
						typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
				}
			}
		}
	}
	}
	
	/**
	 * 对SQL参数(?)设值,参考org.apache.ibatis.executor.parameter.
	 * DefaultParameterHandler
	 * 
	 * @param ps
	 * @param mappedStatement
	 * @param boundSql
	 * @param parameterObject
	 * @throws SQLException
	 * @author alex (90167)
	 * @return 
	 */
	private HashMap<String, String> setParametersNew(MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject) throws SQLException {
		HashMap<String, String> map = new HashMap<>();

		try {
			ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
			List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
			if (parameterMappings != null) {
				Configuration configuration = mappedStatement.getConfiguration();
				TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
				MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
	 
				for (int i = 0; i < parameterMappings.size(); i++) {
					ParameterMapping parameterMapping = parameterMappings.get(i);

					if (parameterMapping.getMode() != ParameterMode.OUT) {
						Object value;
						String propertyName = parameterMapping.getProperty();

						PropertyTokenizer prop = new PropertyTokenizer(propertyName);
						if (parameterObject == null) {
							value = null;
						} else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
							value = parameterObject;
						} else if (boundSql.hasAdditionalParameter(propertyName)) {
							value = boundSql.getAdditionalParameter(propertyName);
						} else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX)
								&& boundSql.hasAdditionalParameter(prop.getName())) {
							value = boundSql.getAdditionalParameter(prop.getName());
							if (value != null) {
								value = configuration.newMetaObject(value)
										.getValue(propertyName.substring(prop.getName().length()));
							}
						} else {
							value = metaObject == null ? null : metaObject.getValue(propertyName);
						}
						TypeHandler typeHandler = parameterMapping.getTypeHandler();
						if (typeHandler == null) {
							throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName
									+ " of statement " + mappedStatement.getId());
						}

						if (map != null) {
							map.put(propertyName, value != null ? value.toString() : "null");
						}
				}
			  }
		    }
		} catch (Exception e) {
			logger.warn("page error," + e.getMessage());
		}
		return map;
	}

	   /**
     * 生成特定数据库的分页语句
     * 
     * @param sql
     * @param page
     * @return
     * @author alex (90167)
     */
    private String pageSql(String sql, Page page) {
        if (page == null || dialect == null || dialect.equals("")) {
            return sql;
        }

        StringBuilder sb = new StringBuilder();
        if ("hsqldb".equals(dialect)) {
            String s = sql;
            sb.append("select limit ");
            sb.append(page.getCurrentResult());
            sb.append(" ");
            sb.append(page.getShowCount());
            sb.append(" ");
            sb.append(s.substring(6));
        } else if ("mysql".equals(dialect)) {
            sb.append(sql);
            sb.append(" limit " + page.getCurrentResult() + "," + page.getShowCount());
        } else if ("oracle".equals(dialect)) {
            sb.append("select * from (select tmp_tb.*,ROWNUM row_id from (");
            sb.append(sql);
            sb.append(")  tmp_tb where ROWNUM<=");
            sb.append(page.getCurrentResult() + page.getShowCount());
            sb.append(") where row_id>");
            sb.append(page.getCurrentResult());
        } else {
            throw new IllegalArgumentException("分页插件不支持此数据库：" + dialect);
        }
        return sb.toString();
    }

	public Object plugin(Object arg0) {
		return Plugin.wrap(arg0, this);
		// 当目标类是CachingExecutor类型时，才包装目标类，否者直接返回目标本身,减少目标被代理的
	    // 次数
	    /*if (arg0 instanceof CachingExecutor) {
	        return Plugin.wrap(arg0, this);
	    } else {
	        return arg0;
	    }*/
	}

	public void setProperties(Properties p) { 
		
	}

	/**
	 * 
	 * <p>
	 * 保存在ThreadLocal中，使 {@link ResultSetInterceptor}能获取到此page对象
	 * </p>
	 * 
	 * @param p
	 * @author alex (90167)
	 */
	private static void set(Page p) {
		PAGE_CONTEXT.set(p);
	}

	/**
	 * <p>
	 * 查询总数
	 * </p>
	 * 
	 * @param ivk
	 * @param ms
	 * @param boundSql
	 * @param param
	 * @param sql
	 * @return
	 * @throws SQLException
	 * @author alex (90167)
	 */
	private int queryTotal(Invocation ivk, MappedStatement ms, BoundSql boundSql, Object param, String sql) throws SQLException {
		int index = sql.indexOf("from ");
		Connection conn = (Connection) ivk.getArgs()[0];
		// String countSql = "select count(0) from (" + sql + ") tmp_count";

		String countSql = this.parseCount(sql);

		BoundSql bs = new BoundSql(ms.getConfiguration(), countSql, boundSql.getParameterMappings(), param);

		ResultSet rs = null;
		PreparedStatement stmt = null;
		int count = 0;
		try {
			stmt = conn.prepareStatement(countSql);
			setParameters(stmt, ms, bs, param, null);
			rs = stmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.getMessage(),ex);
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
		return count;
	}

	public static String parseCount(String sql) {
		String sql2 = sql;
		try {
			SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcUtils.MYSQL);
			List<SQLStatement> stmtList = parser.parseStatementList(); //
			SQLStatement stmt = stmtList.get(0);
			
			if (stmt instanceof SQLSelectStatement) {
				SQLSelectStatement sstmt = (SQLSelectStatement) stmt;
				SQLSelect sqlselect = sstmt.getSelect();
				SQLSelectQuery sqlSelectQuery = sstmt.getSelect().getQuery();
				
				if (sqlSelectQuery instanceof SQLSelectQueryBlock) {
					SQLSelectQueryBlock query = (SQLSelectQueryBlock) sqlselect.getQuery();

					SQLSelectQueryBlock query2 = new SQLSelectQueryBlock();

					query2.setFrom(query.getFrom());
					query2.setGroupBy(query.getGroupBy());
					//query2.setDistionOption(query.getDistionOption());
					query2.setInto(query.getInto());
					query2.setParent(query.getParent());
					query2.setWhere(query.getWhere());

					SQLSelectItem item = new SQLSelectItem();
					item.setExpr(new SQLCharExpr("1"));
					query2.addSelectItem(item);
					SQLSelect sqlSelect = new SQLSelect();
					sqlSelect.setQuery(query2);

					sstmt.setSelect(sqlSelect);
					if(query.getDistionOption()==0){
						sql = sstmt.toString();
					}else{
						sql = sql2 ;
					}

				}else if(sqlSelectQuery instanceof SQLUnionQuery){
					//MySqlUnionQuery unionQuery = (MySqlUnionQuery)sqlSelectQuery;
 					//MySqlSelectQueryBlock left = (MySqlSelectQueryBlock)unionQuery.getLeft();
					//MySqlSelectQueryBlock right = (MySqlSelectQueryBlock)unionQuery.getLeft();
					//System.out.println();
				}
			}
		} catch(Exception ex){
			logger.error("", ex);
		}finally{
			sql = "select count(1) from (" + sql + ") tmp_count ";
		}
		
		return sql;
	}
	
}
