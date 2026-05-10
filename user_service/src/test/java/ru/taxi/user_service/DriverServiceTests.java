package ru.taxi.user_service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.taxi.user_service.dto.DriverResponse;
import ru.taxi.user_service.model.Driver;
import ru.taxi.user_service.model.DriverStatus;
import ru.taxi.user_service.repository.DriverRepository;
import ru.taxi.user_service.service.DriverService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor
class DriverServiceTests {
	private DriverService service;
	private DriverRepository repository;


	@Test
	void testConcurrentAssignment_OnlyOneDriverAssigned() {
		// Given: создаём 3 свободных водителей
		createDrivers(3, DriverStatus.FREE);

		// When: 10 потоков одновременно пытаются назначить водителя
		ExecutorService executor = Executors.newFixedThreadPool(10);
		List<Future<DriverResponse>> futures = IntStream.range(0, 10)
				.mapToObj(i -> executor.submit(() -> service.assignFreeDriver()))
				.toList();

		// Then: только один поток успешно назначил водителя
		long successCount = futures.stream()
				.filter(future -> {
					try {
						future.get();
						return true;
					} catch (Exception e) {
						return false;
					}
				})
				.count();

		assertThat(successCount).isEqualTo(1);

		// Проверяем, что остальные водители остались свободными
		List<Driver> freeDrivers = repository.findByStatus(DriverStatus.FREE);
		assertThat(freeDrivers).hasSize(2);

		executor.shutdown();
	}

	private void createDrivers(int count, DriverStatus status) {
		for (int i = 0; i < count; ++i) {
			Driver driver = new Driver();
			driver.setName("Driver_" + i);
			driver.setStatus(status);
			repository.save(driver);
		}
	}
}
