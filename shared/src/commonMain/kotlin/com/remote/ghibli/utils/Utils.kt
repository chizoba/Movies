package com.remote.ghibli.utils

// The assumption here is that if any referenced entity url (e.g. `peopleUrls`, `speciesUrl`) points
// to a base entity endpoint (e.g. `/people/`, `/species/`), then there should be only 1 url in
// the referenced list of urls, which points to all entities.
fun urlPointsToAllEntities(urls: List<String>): Boolean =
    urls.size == 1 && getIdFromUrl(urls.first()).isEmpty()

fun getIdFromUrl(url: String): String = url.split("/").last()
