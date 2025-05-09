package com.url.shortener.service;

import com.url.shortener.dto.CreateShortUrlDto;
import com.url.shortener.entity.Url;

public interface IUrlService {
    public CreateShortUrlDto createShortUrl(Url url);
}
