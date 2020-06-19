// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
 
package com.google.sps;
 
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
 
public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    //throw new UnsupportedOperationException("TODO: Implement this method.");
    //method 1: start with the full day and substract time for each event
        //start with the mandatory attendees - save as option 1
        //for each optional attendee you can substract time
        //want to maximize the number of people invited so will have to try different combinations
        //start with person with least events
        //get events for each person
    //create a function that has an allday timerange at first and then progressively splits it up

    //create an all day timerange
    ArrayList<TimeRange> freeSlots = new ArrayList<TimeRange>();
    freeSlots.add(TimeRange.WHOLE_DAY);

    Collection<String> mandatoryAttendees = request.getAttendees();
    ArrayList<Event> mandatoryEvents = new ArrayList<Event>();
    ArrayList<Event> optionalEvents = new ArrayList<Event>();
    //organize all the mandatory and optional events
    for(Event event: events){
        if(checkForOverlap(mandatoryAttendees,event.getAttendees())){
            mandatoryEvents.add(event);
        }else{
            optionalEvents.add(event);
        }
    }

    //remove all the times of the mandatory events from the total timerange
    for(Event event: mandatoryEvents){
        removeConflict(freeSlots,event);
        cleanFreeSlots(freeSlots,request.getDuration());
    }

    return freeSlots;
  }

  private boolean checkForOverlap(Collection<String> mandatory, Set attendees){
      //compares to see if there is overlap in the attendees of a collection and set
      for(String person:attendees){
          if(mandatory.contains(person)){
              return true;
          }
      }

      return false;
  }

  private ArrayList<TimeRange> removeConflict(ArrayList<TimeRange> freeSlots, Event event){
    TimeRange takenSlot = event.getWhen();
    int takenSlotStart = takenSlot.start();
    int takenSlotEnd = takenSlot.end();

    for(TimeRange slot: freeSlots){
        if(slot.start() <= takenSlotStart && slot.end() >= takenSlotEnd){
          //  |------free--------|
          //     |--event--|
          int slotIndex = freeSlots.indexOf(slot);
          TimeRange priorSlotTime = TimeRange.fromStartEnd(slot.start(),takenSlotStart,false);
          TimeRange postSlotTime = TimeRange.fromStartEnd(slot.end(),takenSlotEnd,false);
          freeSlots.remove(slot);
          freeSlots.add(slotIndex,postSlotTime);
          freeSlots.add(slotIndex,priorSlotTime);
        }else if(slot.start() <= takenSlotStart && slot.end() <= takenSlotEnd){
          // |-----free----|
          //      |----event---|
          int slotIndex = freeSlots.indexOf(slot);
          TimeRange priorSlotTime = TimeRange.fromStartEnd(slot.start(),takenSlotStart,false);
          freeSlots.remove(slot);
          freeSlots.add(slotIndex,priorSlotTime);
        }else if(slot.start() >= takenSlotStart && slot.end() >= takenSlotEnd){
          //      |----free-----|
          // |---event----|
          int slotIndex = freeSlots.indexOf(slot);
          TimeRange postSlotTime = TimeRange.fromStartEnd(slot.end(),takenSlotEnd,false);
          freeSlots.remove(slot);
          freeSlots.add(slotIndex,postSlotTime);
        }else{
          // no overlap
          // |--free--| |--event--|
          // free slot not big enough
          //     |--free--|
          // |-----event------|
        }

        return freeSlots;
    }
  }

  private ArrayList<TimeRange> cleanFreeSlots(ArrayList<TimeRange> freeSlots, long duration){
      //goes through the freeSlots and removes any timeranges less than the duration of the meeting
      for(TimeRange slot:freeSlots){
          if(slot.duration() < duration){
              freeSlots.remove(slot);
          }
      }

      return freeSlots;
  }
}
 

