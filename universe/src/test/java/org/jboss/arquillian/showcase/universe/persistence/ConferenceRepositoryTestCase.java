/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.showcase.universe.persistence;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.CleanupStrategy;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.showcase.universe.Deployments;
import org.jboss.arquillian.showcase.universe.Models;
import org.jboss.arquillian.showcase.universe.model.Conference;
import org.jboss.arquillian.showcase.universe.repository.ConferenceRepository;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * ConferenceRepositoryTestCase
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
@RunWith(Arquillian.class)
public class ConferenceRepositoryTestCase {

    @Deployment
    public static JavaArchive deploy() {
        return Deployments.Backend.conference();
    }

    @EJB
    private ConferenceRepository repository;

    @Test @InSequence(1)
    @ShouldMatchDataSet(value = "conference_with_speaker.yml", excludeColumns = {"id", "conference_id", "speakers_id"})
    @Cleanup(strategy = CleanupStrategy.USED_TABLES_ONLY, phase = TestExecutionPhase.AFTER)
    public void shouldBeAbleToPersistConference() throws Exception {
        final Conference conference = Models.createConference(); 
        conference.addSpeaker(Models.createUser());

        repository.store(conference);
    }

    @Test @InSequence(2)
    @UsingDataSet("conference_with_speaker.yml")
    @Cleanup(strategy = CleanupStrategy.USED_TABLES_ONLY, phase = TestExecutionPhase.AFTER)
    public void shouldBeAbleToLoadConference() throws Exception {
        Conference conference = repository.get("10");
        
        Assert.assertEquals("JavaZone 2012", conference.getName());
    }
}
