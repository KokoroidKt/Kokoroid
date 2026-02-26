/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.adapterApi.utils

import dev.kokoroidkt.adapterApi.adapter.Adapter
import dev.kokoroidkt.adapterApi.adapter.AdapterMeta
import dev.kokoroidkt.adapterApi.adapter.AdapterRegistry
import dev.kokoroidkt.driverApi.driver.DriverRegistry
import org.koin.mp.KoinPlatform.getKoin
import java.sql.Driver

fun Adapter.getId(): String? = getKoin().get<AdapterRegistry>().getAdapterId(this::class.java)

@Suppress("UNCHECKED_CAST")
fun <T : Driver> getDriver(clazz: Class<out T>): T? = getKoin().get<DriverRegistry>()[clazz] as T?

fun Adapter.getMetadata(): AdapterMeta? = this.getId()?.let { getKoin().get<AdapterRegistry>()[it]?.metadata }
