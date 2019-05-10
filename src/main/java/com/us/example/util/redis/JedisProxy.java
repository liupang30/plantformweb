package com.us.example.util.redis;


import org.apache.log4j.Logger;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisProxy {
	private static Logger logger = Logger.getLogger(JedisProxy.class);
	private  static JedisPool jedisPool;
    private JedisProxy() {
    	//1.设置连接池的配置对象
        JedisPoolConfig config = new JedisPoolConfig();
        //设置池中最大连接数
        config.setMaxTotal(50);
        //设置空闲时池中保有的最大连接数
        config.setMaxIdle(20);
        //2.设置连接池对象
        jedisPool = new JedisPool(config,"140.143.194.144",6579);
    }
    public static class SingletonHolder{
		private static final JedisProxy INSTANCE = new JedisProxy();
    }
    public static final JedisProxy getInstance(){
		return SingletonHolder.INSTANCE;
    }
    public Jedis createProxy() {
        Enhancer enhancer = new Enhancer();
        //设置代理的父类，就设置需要代理的类
        enhancer.setSuperclass(Jedis.class);
        //设置自定义的代理方法
        Callback callback = new JedisHandler(jedisPool);
        enhancer.setCallback(callback);
 
        Object o = enhancer.create();
        Jedis jedis = null;
        if (o instanceof Jedis){
            jedis = (Jedis) o;
        }
        return jedis;
    }
    public static void getActiveNum(){
		logger.info("***************pool active num******************"+jedisPool.getNumActive());
	}
    public static void main(String[] args){
    	try {
			JedisProxy.getInstance().createProxy().set("age", "20");
			System.out.println(JedisProxy.getInstance().createProxy().get("age"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
