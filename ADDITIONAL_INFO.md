#SPRING BOOT İLE MULTİ THREADING

Spring boot da multithreading işlemi daha basit ve kullanışlıdır. Spring boot multithreading işlemi için öncelikle bir configuration sınıfı oluşturmalıyız. Bu sınıf aşağıda ki gibi olabilir.

    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.scheduling.annotation.EnableAsync;
    import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
    import java.util.concurrent.Executor;
    
    @Configuration
    @EnableAsync
    public class AsyncConfiguration {
        private static final Logger LOGGER = LoggerFactory.getLogger(AsyncConfiguration.class);
        @Bean (name = "taskExecutor")
        public Executor taskExecutor() {
            LOGGER.debug("Creating Async Task Executor");
            final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(2);
            executor.setMaxPoolSize(2);
            executor.setQueueCapacity(100);
            executor.setThreadNamePrefix("CarThread-");
            executor.initialize();
            return executor;
        }
    }

Yukarıda ki sınıf asenkron olarak yürütmeyi çalıştırmak ve etkinleştirmek için gereklidir. 'taskExecutor' isimli bean anotasyonu ile bir uygulama için thread sayısı, kuyruk sınırı boyutu vb. gibi yapılandırma ve özelleştirme gibi imkanları bize sunuyor. Spring boot uygulaması çalıştığında öncelikle bu bean anotasyonunu arayacaktır ve eğer tanımlı değilse varsayılan olarak SimpleAsyncTaskExecutor oluşturulacaktır.

Servis sınıfında ki iş kodlarını asenkron olarak çalıştırmak için aşağıda ki gibi @Async anotasyonundan faydalanırız.

        @Service
        public class CarService {
    
        private static final Logger LOGGER = LoggerFactory.getLogger(CarService.class);
    
        @Autowired
        private CarRepository carRepository;
    
        @Async
        public CompletableFuture<List<Car>> saveCars(final InputStream inputStream) throws Exception {
            final long start = System.currentTimeMillis();
    
            List<Car> cars = parseCSVFile(inputStream);
    
            LOGGER.info("Saving a list of cars of size {} records", cars.size());
    
            cars = carRepository.saveAll(cars);
    
            LOGGER.info("Elapsed time: {}", (System.currentTimeMillis() - start));
            return CompletableFuture.completedFuture(cars);
        }
    
        private List<Car> parseCSVFile(final InputStream inputStream) throws Exception {
            final List<Car> cars=new ArrayList<>();
            try {
                try (final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line=br.readLine()) != null) {
                        final String[] data=line.split(";");
                        final Car car=new Car();
                        car.setManufacturer(data[0]);
                        car.setModel(data[1]);
                        car.setType(data[2]);
                        cars.add(car);
                    }
                    return cars;
                }
            } catch(final IOException e) {
                LOGGER.error("Failed to parse CSV file {}", e);
                throw new Exception("Failed to parse CSV file {}", e);
            }
        }
    
        @Async
        public CompletableFuture<List<Car>> getAllCars() {
    
            LOGGER.info("Request to get a list of cars");
    
            final List<Car> cars = carRepository.findAll();
            return CompletableFuture.completedFuture(cars);
        }
    }

Yukarıda görüldüğü gibi 3 adet metod vardır. Bunlar toplu ekleme, listeleme ve parse etme işleri yapmaktadır. Toplu ekleme ve listeleme işlemleri asenkron olarak tanımlanmıştır. Yani toplu birden fazla bir csv dosyası ve bu csv dosyalarının içinde de birden fazla kayıt varsa eş zamanlı olarak veritabanına kaydedecektir. Yalnız configure sınıfımızda biz en fazla 2 threadin çalışacağını belirttiğimiz için aynı anda iki thread çalışacaktır ve aynı anda iki kayıt eklenecektir.
