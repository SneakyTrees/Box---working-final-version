/*
    Alright, time to refine this framework. I should make another class for this list, but fuck it I'm lazy.
    
    1. First priority is porting over the proper box class (not that perfectBox one) and
    cleaning it up.

    2. Second priority is to redo the Event series classes, refining structure into the following:
        Parent 1: Event
            -EventManager manager;
            -Screen screen
            *public abstract boolean executeEvent();
        
        Child I 1. - InstantEvent                               Child I 2. - DeltaEvent
            -runs for just one frame                                -runs over at least one frame, almost always more
                                                                    -takes its manager's timeDelta and uses it to express time in game (ex. s*t=d)
                                                                Child II 1. - TimerEvent
                                                                    -every set interval of time, execute code placed in method outside executeEvent();
                                                                    -may expire (in that case remainingDelta is calc'd and passed to meth before returning 
                                                                    false) or may be neverending

                                                                  
    3. Fuck that was hard to format. Anyway, third priority is equipping the game to handle Coordinates existing in all quadrants.
    Aaaand that's going to be incredibly fucking annoying... lotta tedious and extra code on this one...


   Started 5/22/2015 afterschool, now typing this at 9:06 on the same day
   LETS GO BOIS GET HYPEEEEEEEEEEEEEEEEE

*/