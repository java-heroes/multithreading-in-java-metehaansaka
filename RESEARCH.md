# Java Thread Safety

## Thread Nedir
    Thread programın eş zamanlı yada paralel olarak
    çalışan, birbirine bağımlı veya bağımsız işlemler
    yapabilen alt parçacıklara nedir. Birden fazla Thread
    oluşturabiliriz. Normalde programlar bir işi bitirmeden
    diğer işe başlamazlar. Ancak threadler sayesinde diğer
    işe başlamak için ilk işin bitmesini beklemeye gerek
    yoktur. İki iş aynı anda yapılabilir.

## Thread Safety Nedir
    Thread safety oluşturduğumuz threadlerin güvenle
    çalışmasını sağlar. Threadlerin aynı anda çalışması
    durumunda threadlerimizi sıraya almamız gerekebilir.
    Birden fazla thread oluşturduysak bu threadler
    çakışabilir veya farklı sorunlar çıkartabilir. Aynı 
    şekilde threadlerimize gelen birden fazla isteği
    kontrol etmemiz için de kullanılır.

## Thread Safety İçin Birçok Yöntem Mevcuttur

***Çoğu durumda multithread uygulamarında ki hatalar durumu birkaç thread arasında yanlış paylaşmanın sonucudur!***

### 1- Durum Bilgisi Olmayan Uygulamalar
    Durum bilgisi olmayan uygulamalar dış durumdan
    etkilenmeyip durumunu koruyan uygulamalardır.
    Bu uygulamalar da belirlenen girdiler verildiğinde
    verilen girdiye göre çıktı üretirler. Sadece belirli
    bir çıktı ürettikleri için güvenle çağırılabilir ve
    diğer uygulamalarla karışmadan ve diğer uygulamalara
    müdahele etmeden kullanılabilir.
    Bu nedenenle durum bilgisi olmayan uygulamalar emniyeti
    sağlamanın en basit yoludur.

### 2- Sabit Uygulamalar
    Eğer diğer thread ve uygulamalara bilgi veya durum
    paylaşmamız gerekiyorsa bu durumları değişmez hale
    getirerek threadlerimizi güvenli hale getirebiliriz.
    Java da bir durumu değişmez hale getirmek basittir.
    Bunu private ile erişimini kısıtladıktan sonra 
    final anahtar sözcüğü ile sabit hale getirebiliriz. 
    Bu durum ancak constructor metoduyla değiştirilebilir. 
    Yani durum dışarıdan sadece salt okunur olmalı. 
    Bu sınıflarda ki durumlar sadece oluşturululurken
    değiştirilebileceği için threadler için emniyetli bir
    yöntemdir.

### 3- Thread-Local Sınıfı
    ThreadLocal sınıfı belirlediğimiz nesnelerin sadece
    aynı thread tarafından erişilebilir olmasını sağlar.
    Bu sayede thread safe olmayan nesneleri thread safe
    kullanmış oluruz. Bir ThreadLocal nesnesi içerisine
    yazdığımız nesne, ThreadLocal‘e yazan thread
    tarafından çalıştırılan tüm methodlar tarafından 
    okunabilir olacaktır. new anahtar kelimesi ile
    yarattığımız her bir yeni ThreadLocal nesnesi Thread
    içerisindeki Map’de farklı bir key olarak tutulmaktadır
    Bu sebeple belirli bir amaç için kullanacağımız 
    ThreadLocal nesnesinin sadece tek bir instance’ının
    olduğundan emin olmalıyız. Bunu da static final olarak
    tanımlayarak sağlayabiliriz.

### 4- Synchronized Koleksiyonlar
    Java Collections içerisinde bulunan synchronized 
    kapsamını kullanarak kolaylıkla güvenli threadler
    oluşturabiliriz. Performans açısından dezavantaja
    sahiptir.
***Synchronized koleksiyonlarının nasıl
kullanılabileceğine [buradan erişebiliriz](https://www.baeldung.com/java-synchronized-collections).***

### 5- Concurrent Koleksiyonlar
    Synchronized koleksiyonuna alternatif olarak
    kullanabiliriz. java.util.concurrent paketi altında
    ConcurrentHashMap gibi birkaç eşzamanlı koleksiyona
    erişebiliriz. Synchronized koleksiyonundan farklı
    olarak concurrent koleksiyonu verilerini segmentlere 
    bölerek thread güvenliği sağlar. Ayrıca synchronized
    koleksiyonlarına göre performans açısından daha
    avantajlıdır.
    Conccurrent ve Synchronized koleksiyonları içeriği
    değil de doğrudan koleksiyonun kendisini güvenli hale
    getirir.
***ConcurrentHashMap kullanımına [buradan](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html) detaylı olarak bakabiliriz.***

### 6- Atomik Nesneler
    AtomicInteger , AtomicLong , AtomicBoolean ve
    AtomicReference dahil olmak üzere Java'nın sağladığı
    atom sınıfları kümesini kullanarak thread güvenliği
    elde etmek de mümkündür.Atomik sınıflar, senkronizasyon
    kullanmadan, thread için güvenli olan atomik işlemleri 
    gerçekleştirmemize izin verir . 
    Tek bir makine düzeyi işlemde bir atomik işlem
    yürütülür.

### 7- Synchronized Anahtar Kelimesi
    Diğer yöntemler thread güvenliği sağlamak için oldukça
    iyi ve yeterli yöntemler olsalar da bazen ek durumlara
    ihtiyaç duyulabilir. Diğer işlerden bu yönteme erişimi
    engellerken aynı anda yalnızca bir thread senkronize
    bir yönteme erişebilir. İlk iş parçacığı bitene veya
    yöntem bir özel durum oluşturana kadar diğer iş
    parçacıkları engellenmiş olarak kalacaktır.
    Bu yöntem bir metodun başına synchronized anahtar
    kelimesi eklenerek gerçekleştirilir.


# Synchronized
**Synchronized yöntemini kullanmayı tercih ettim. Bu yöntemin Hangi zamanlarda ve nasıl çalıştığını açıklamaya çalıştım.**

Senkronizasyon, belirli bir görevi tamamlamak için bir seferde yalnızca bir thread'e izin verme işlemidir. Aynı anda birden fazla thread çalıştığında ve aynı kaynağa aynı anda erişmek istediğinde, tutarsızlık sorununun ortaya çıkabilir. Bu nedenle senkronizasyon, bir seferde yalnızca bir thread'e izin vererek tutarsızlık sorununu çözmek için kullanılır.


### Fonksiyon

Aşağıda ki functions sınıfında count isimli bir değişken ve getCount isimli bir metod bulunmaktadır. getCount metodu senkronize bir metoddur ve her çağırıldığında count isimli değişkeni bir arttırmaktadır.

    public class Functions {
        int count = 0;
    
        public synchronized void getCount(){
            System.out.println(count++);
        }
    }

### Subclass

Aşağıda ki subclass Thread sınıfından türemiştir. İçerisinde functions sınıfının örneğini ve run metodunu bulundurmaktadır. Run metodu getCount metodunu 1000 defa çalıştırmaktadır.

    public class SubClass extends Thread{
    Functions functions = new Functions();

    @Override
    public void run(){
        for (int i = 0; i < 1000; i++){
            functions.getCount();
        }
    }

}

### Main

Aşağıdaki main metodunda iki adet thread oluşturup ikisine de aynı functions sınıfının örneğini veriyoruz. Eğer getCount metodumuz senkronize olmasaydı iki thread aynı anda çalışacağı için sıralı bir sonuç elde edemeyebilirdik. Ancak bu yöntem sayesinde bir metoda aynı anda bir thread erişebileceği için diğer thread getCount metoduna erişebilmek için ilk threadin o metodla işini bitirmesini beklemek zorundadır.

    public static void main(String[] args) {
        SubClass subClass = new SubClass();

        Thread thread1 = new Thread(subClass);
        Thread thread2 = new Thread(subClass);

        thread1.start();
        thread2.start();

    }

Yani eğer getCount m
etodu senkronize olmasaydı sayılar sıralı olmayabilirdi. Ancak şuan sayılar 0'dan 1999'a kadar sıralanmış olacaktır.