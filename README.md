Android-CircularRevealFragment
==============================

A demo about how to show and hide fragments using the new Android Lollipop circle reveal view animations.

A fragment is essentially a view, but it is displayed using a TragmentTransaction, which supports references to animations defined in resources, but circle reveal animations can't be defined in resources, so this is one solution. 

The fragment takes care of displaying itself using the ViewAnimationUtils.createCircularReveal as soon as the view has been layout. It receives the center of the circle using fragment arguments bundle, which is fixed now, but you can change it. The button just adds a new fragment to the linear layout, the circle reveal animation is done by the fragment.

The fragment also can prepare the unreveal animation based on a touch point and calculates the proper cicle radius to start the animation by calculating the longest distance from the touch point to all four corners of the view. 

It also shows how to use an interface to comunicate the fragment with the activity.

Minimum API level is 21, the AppCompat library doesn't support circle reveals, so even if you use it, you won't see the animations. The code doesn't provide a fallback solution, but you could implement ScaleAnimation or fade in/out.


