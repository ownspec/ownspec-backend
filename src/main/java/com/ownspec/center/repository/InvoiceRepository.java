package com.ownspec.center.repository;

import com.ownspec.center.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by lyrold on 09/10/2016.
 */
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
