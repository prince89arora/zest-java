/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package org.apache.zest.spi.cache;

/**
 * A CachePool is a service that manages the Persistence Caches.
 * <p>
 * The CachePool is typically implemented as a Zest Extension, and is an optional extension in the persistence
 * subsystem of Zest. If a Cache Extension is not provided, caching will be turned off. However, since caching
 * operate on EntityStore level, and is an optional component at that, just because you have defined a Cache
 * Extension does not necessary mean that your system will use it. Check the EntityStore implementations for
 * details if they are Cache enabled. Most EntityStore implementations has this enabled, often via the MapEntityStore
 * and JSONMapEntityStore SPI.
 * </p>
 * <p>
 * NOTE: Make sure that there is a match between the fetchCache and returnCache methods, to ensure no memory leakage
 * occur. Also remember that if the reference count reaches zero, the CachePool will destroy the Cache as soon
 * as possible and a new fetchCache will return an empty one.
 * </p>
 */
public interface CachePool
{

    /**
     * Fetches a cache from the pool.
     * If the cache does not exist already, then a new Cache should be created and returned. For each fetchCache()
     * call, a reference count on the Cache must be increased.
     *
     * @param cacheId   The identity of the cache. If the same id is given as a previous fetch, the same cache will be
     *                  returned.
     * @param valueType
     * @param <T>
     *
     * @return The cache fetched from the pool.
     */
    <T> Cache<T> fetchCache( String cacheId, Class<T> valueType );

    /**
     * Returns the cache back to the pool.
     * The reference count for the cache must then be decreased and if the count reaches zero, the Cache should be
     * destroyed and cleared from memory.
     *
     * @param cache The cache to return to the pool.
     */
    void returnCache( Cache<?> cache );
}
