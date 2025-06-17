package com.AFM.AML.Course.repository;

import com.AFM.AML.Course.models.PostLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostLinkRepo extends JpaRepository<PostLink, Integer> {

    @Query(value = "select * from post_link where invoice_id = ?1",nativeQuery = true)
    PostLink getPostLinksByInvoiceID(String invoice_id);
}
