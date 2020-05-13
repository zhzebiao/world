- [x] <!-- 第 1 章《Spring Boot 入门》 -->

  - chapter-1-spring-boot-quickstart

    @RestController = @Controller+@ResponseBody

    @RequestMapping(value={{path}},method=RequestMethod.GET)
  
- [x] <!-- 第 2 章《配置》 -->

  - chapter-2-spring-boot-config

    @EnableSwagger2Doc ： 在Application上打开Swagger Api文档自动生成功能；

    @Value("${demo.book.name}") ：可以用application.properties或者application.yml中的值注入到变量中；
  
- [x] <!-- 第 3 章《Web 开发》 -->

  - chapter-3-spring-boot-web

    @Service：与Controller和Repository相同

    @RequestBody：将Json解析成对象

    @PathVariable：读取Url路径中用{}框起来的指定变量

    @ModelAttribute：读取x-www-form-urlencoded后的键值对，解析成对象
    
    @RequestParam：读取x-www-form-urlencoded后的键值对
  
- [x] <!-- 第 4 章《模板引擎》 -->

  - chapter-4-spring-boot-web-thymeleaf
  
- [x] <!-- 第 4 章表单校验案例 -->
  - chapter-4-spring-boot-validating-form-input

    @Valid，@NotNull，@NotEmpty，@Min，@Max：对对象进行格式检测
  
- [x] <!-- 第 5 章《数据存储》 -->

  - chapter-5-spring-boot-data-jpa
  
    extends JpaRepository
  
- [x] <!-- 第 5 章数据分页排序案例 -->

  - chapter-5-spring-boot-paging-sorting

    extends PagingAndSortingRepository

    Pagable:pageSize,pageNumber

- [x] <!-- Spring Data ES 篇 -->

  - spring-data-elasticsearch-crud

    extends ElasticsearchRepository

    @Document(indexName="index",type="_doc")

  - spring-data-elasticsearch-query

- [x] <!-- Spring Boot 之配置文件详解 -->

  - springboot-configuration
  
    @Configuration：注明这个是一个注解类
  
    @Bean：往Spring容器中注入一个Bean对象
  
- [ ] <!-- Spring Boot 整合 Dubbo/ZooKeeper 详解 SOA 案例 -->
  - springboot-dubbo-server
  -  springboot-dubbo-client
  
- [x] <!-- Spring Boot 整合 Elasticsearch -->
  
  - springboot-elasticsearch
  
- [x] <!-- Spring Boot 集成 FreeMarker -->
  
  - springboot-freemarker
  
- [x] <!-- Spring Boot 整合 HBase -->
  
  - springboot-hbase
  
- [x] <!-- Spring Boot 之 HelloWorld 详解 -->
  
  - springboot-helloworld
  
- [ ] <!-- 数据缓存篇 -->

- [x] <!-- Spring Boot 整合 Mybatis 的完整 Web 案例 -->
  
  - springboot-mybatis
  
    @MapperScan("org.spring.springboot.dao") ：在Application上添加Mapper文件扫描
  
    ```markdown
    当主键是自增的情况下，添加一条记录的同时，其主键是不能使用的，但是有时我们需要该主键，这时我们该如何处理呢？这时我们只需要在其对应xml中加入以下属性即可：
    
    useGeneratedKeys="true" keyProperty="对应的主键的对象"。
    
    <mapper namespace="org.spring.springboot.dao.CityDao">
    	<resultMap id="BaseResultMap" type="org.spring.springboot.domain.City">
    		<result column="id" property="id"/>
    		<result column="province_id" property="provinceId" />
    		<result column="city_name" property="cityName" />
    		<result column="description" property="description" />
    	</resultMap>
    	
    	<sql id="Base_Column_List">
    		id, province_id, city_name, description
    	</sql>
    
    	<select id="findByName" resultMap="BaseResultMap" parameterType="java.lang.String">
    		select
    		<include refid="Base_Column_List" />
    		from city
    		where city_name = #{cityName}
    	</select>
    </mapper>
    ```
  
- [x] <!-- Spring Boot 整合 Mybatis Annotation 注解案例 -->
  
  - springboot-mybatis-annotation
  
    @Mapper：标记为Mybatis的Mapper
  
    ```java
    @Mapper
    public interface CityDao{
        @Select("SELECT * FROM city")
        @Results({
            @Result(property="id",column="id"),
            @Result(property = "provinceId", column = "province_id"),
            @Result(property = "cityName", column = "city_name"),
            @Result(property = "description", column = "description"),
        })
        City findByName(@Param("cityName") String cityName);
    }
    ```
  
    
  
- [x] <!-- Spring Boot 整合 Mybatis 实现 Druid 多数据源配置 -->
  
  - springboot-mybatis-mutil-datasource
  
    ```properties
    # application.properties
    ## master 数据源配置
    master.datasource.url=jdbc:mysql://localhost:3306/springbootdb?useUnicode=true&characterEncoding=utf8
    master.datasource.username=root
    master.datasource.password=123456
    master.datasource.driverClassName=com.musql.jdbc.Driver
    
    ## cluster 数据源配置
    cluster.datasource.url=jdbc:mysql://localhost:3306/springbootdb_cluster?useUnicode=true&characterEncoding=utf8
    cluster.datasource.username=root
    cluster.datasource.password=123456
    cluster.datasource.driverClassName=com.mysql.jdbc.Driver
    ```
  
    
    
    ```markdown
    # 不同数据源的mapper文件分包存放
    master数据源的mapper文件存放位置：resources/mapper/master/*
  cluster数据源的mapper文件存放位置：resources/mapper/cluster/*
    ```
    
    
    
    ```java
    # 添加不同数据源的配置类，下面以cluster数据源为例进行配置类的讲解
    @Configuration
    // 扫描 Mapper 接口并容器管理
    // basePackages为cluster配置类的PACKAGE常量值
    @MapperScan(basePackages = ClusterDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "clusterSqlSessionFactory")
    public class ClusterDataSourceConfig {
    
        // 精确到 cluster 目录，以便跟其他数据源隔离
        static final String PACKAGE = "org.spring.springboot.dao.cluster";
        static final String MAPPER_LOCATION = "classpath:mapper/cluster/*.xml";
    
        @Value("${cluster.datasource.url}")
        private String url;
    
        @Value("${cluster.datasource.username}")
        private String user;
    
        @Value("${cluster.datasource.password}")
        private String password;
    
        @Value("${cluster.datasource.driverClassName}")
        private String driverClass;
    
        @Bean(name = "clusterDataSource")
        public DataSource clusterDataSource() {
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setDriverClassName(driverClass);
            dataSource.setUrl(url);
            dataSource.setUsername(user);
            dataSource.setPassword(password);
            return dataSource;
        }
    
        @Bean(name = "clusterTransactionManager")
        public DataSourceTransactionManager clusterTransactionManager() {
            return new DataSourceTransactionManager(clusterDataSource());
        }
    
        @Bean(name = "clusterSqlSessionFactory")
        public SqlSessionFactory clusterSqlSessionFactory(@Qualifier("clusterDataSource") DataSource clusterDataSource)
                throws Exception {
            final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
            sessionFactory.setDataSource(clusterDataSource);
            sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                    .getResources(ClusterDataSourceConfig.MAPPER_LOCATION));
          return sessionFactory.getObject();
        }
    }
    ```
    
    
  
- [x] <!-- Spring Boot 整合 Redis 实现缓存 -->

  - springboot-mybatis-redis

    ```java
    @Autowired
    private RedisTemplate redisTemplate;
    
    ValueOperations<String,City> operations = redisTemplate.opsForValue();
    operations.hasKey(key);
    operations.get(key);
    operations.set(key,city,10,TimeUnit.SECONDS);
    operations.delete(key);
    ```

    

- [ ] <!-- Spring Boot 注解实现整合 Redis 实现缓存 -->

  - springboot-mybatis-redis-annotation

    @Cacheable：检查value是否存在，不存在则新增，存在则不操作；

    @CachePut：直接替换value所缓存的值；

    @CacheEvict：清除value所缓存的值；

    ```java
    @Service
    public class EmployeeService {
    
        @Autowired
        EmployeeMapper employeeMapper;
        /**
         *  @Cacheable
         * 将方法的运行结果进行缓存，如果需要相同的数据，就可以直接从缓存中获取
         * 不用调用方法
         *
         * CacheManager管理多个Cache组件，对缓存真正的CRUD操作在Cache组件中，每一个缓存组件
         * 有自己唯一一个名字
         * value/cacheNames:指定缓存组件的名字
         * key ;缓存数据时使用的key；可以用他来指定，默认使用方法参数的值  1-方法返回值
         *       编写SPEL表达式 #id：参数id的值， #a0,#p0,#root.args[0]
         * keyGenerator key的生成器，可以自己指定key的生成器的组件ID
         *      key/keyGenerator 二选一
         *
         * cacheManager 指定缓存管理器，或者指定缓存解析器cacheResolver
         *
         * condition 指定复合条件的情况下才缓存
         *
         * unless 否定缓存，当unless指定的条件为true，方法的返回值就不会缓存，可以获取到结果进行判断
         *
         * sync 是否使用异步模式
         * @param id
         * @return
         */
        @Cacheable(cacheNames = {"emp"})
        public Employee getEmp(Integer id){
            System.out.println("查询"+id+"号员工");
            return employeeMapper.getEmpById(id);
        }
    }
    ```

    

- [x] <!-- Spring Boot 实现 Restful 服务，基于 HTTP / JSON 传输 -->

  -  springboot-restful

- [x] <!-- Spring Boot 之配置文件详解 -->

  -  springboot-properties

    ```markdown
    # 指定当前可用的配置文件
    spring.profiles.active=dev
    ```

    

- [ ] <!-- Spring Boot HTTP over JSON 的错误码异常处理 -->

  - springboot-validation-over-json

    ```java
    public interface ErrorInfoInterface {
        String getCode();
        String getMessage();
    }
    # Error Code Enum
    public enum GlobalErrorInfoEnum implements ErrorInfoInterface{
        SUCCESS("0", "success"),
        NOT_FOUND("-1", "service not found");
    
        private String code;
    
        private String message;
    
        GlobalErrorInfoEnum(String code, String message) {
            this.code = code;
            this.message = message;
        }
        // getter() & setter()
    }
    
    # Exception
    public class GlobalErrorInfoException extends Exception {
    
        private ErrorInfoInterface errorInfo;
    
        public GlobalErrorInfoException (ErrorInfoInterface errorInfo) {
            this.errorInfo = errorInfo;
        }
        // getter() & setter()
    }
    
    #Error/Exception Handler
    @RestControllerAdvice  //使用切面技术
    public class GlobalErrorInfoHandler {
    
        @ExceptionHandler(value = GlobalErrorInfoException.class)
        public ResultBody errorHandlerOverJson(HttpServletRequest request,
                                               GlobalErrorInfoException exception) {
            ErrorInfoInterface errorInfo = exception.getErrorInfo();
            ResultBody result = new ResultBody(errorInfo);
            return result;
        }
    }
    
    public class ResultBody {
        /**
         * 响应代码
         */
        private String code;
    
        /**
         * 响应消息
         */
        private String message;
    
        /**
         * 响应结果
         */
        private Object result;
    
        public ResultBody(ErrorInfoInterface errorInfo) {
            this.code = errorInfo.getCode();
            this.message = errorInfo.getMessage();
        }
    
        public ResultBody(Object result) {
            this.code = GlobalErrorInfoEnum.SUCCESS.getCode();
            this.message = GlobalErrorInfoEnum.SUCCESS.getMessage();
            this.result = result;
        }
    	// getter() && setter()
    }
    
    ```

    

- [ ] <!-- Spring Boot 2.0 WebFlux -->

- [ ] <!-- Spring Boot WebFlux 快速入门 -->

- springboot-webflux-1-quickstart

- [ ] <!-- Spring Boot WebFlux 实现 Restful 服务，基于 HTTP / JSON 传输 -->

- springboot-webflux-2-restful

- springboot-webflux-3-mongodb

- springboot-webflux-4-thymeleaf

- springboot-webflux-5-thymeleaf-mongodb

- springboot-webflux-6-redis

- springboot-webflux-7-redis-cache