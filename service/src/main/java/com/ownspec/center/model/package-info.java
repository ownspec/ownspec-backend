@GenericGenerator(name = MainSequenceConstants.SEQUENCE_GENERATOR_NAME, strategy = "enhanced-sequence", parameters = {
        @Parameter(name = SEQUENCE_PARAM, value = "MAIN_SEQUENCE"),
        @Parameter(name = OPT_PARAM, value = "pooled-lo"),
        @Parameter(name = INCREMENT_PARAM, value = "50")}) package com.ownspec.center.model;

import static org.hibernate.id.enhanced.SequenceStyleGenerator.INCREMENT_PARAM;
import static org.hibernate.id.enhanced.SequenceStyleGenerator.OPT_PARAM;
import static org.hibernate.id.enhanced.SequenceStyleGenerator.SEQUENCE_PARAM;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

