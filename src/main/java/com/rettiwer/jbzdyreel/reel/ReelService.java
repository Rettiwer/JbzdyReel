package com.rettiwer.jbzdyreel.reel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReelService {

    @Autowired
    private ReelRepository reelRepository;

    public List<Reel> getReelsPage(Integer pageNo)
    {
        Pageable paging = PageRequest.of(pageNo, 5, Sort.by("createdAt").descending());

        Page<Reel> pagedResult = reelRepository.findAll(paging);

        if(pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<Reel>();
        }
    }
}
