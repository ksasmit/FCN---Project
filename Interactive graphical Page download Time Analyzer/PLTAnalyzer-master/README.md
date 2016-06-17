# PLTAnalyzer
Mobile platform to analyse PLT over multiple NSS

Problem statement/motivation:
Web page load time is a key performance metric that many techniques aim to reduce. Numerous studies and media articles report its importance for user experience and consequently to business revenues. For example, Amazon increased revenue 1% for every 0.1 second reduction in PLT, and Shopzilla experienced a 12% increase in revenue by reducing PLT from 6 seconds to 1.2 seconds. We observe many factors deals with Web page load time including web page complexity, number of downloaded objects, browser computation, caching levels and many more. Many of these mentioned factors have been quite well addressed by SPDY paper. Here in this project, we would like to stress more on variability of page load time with respect to network Signal Strengths (NSS). Network Signal Strength is one of key factor which significantly affect the page load time. Low signal strength might considerably reduce the page load performance even for simple, less complex web pages. Same web page might incur different PLT with different network providers, given signal strengths. It might be the case for a web page to take lower PLT for a lesser signal strength of network A rather than network B. There might be a better bandwidth support for selective web pages by particular network. 
For Example: WWW.GOOGLE.COM (homepage) has considerably less complex and high availability web page; still low internet signal strength can significantly affect its page load time.

Our Approach:
We target to design and develop a location agnostic Android application platform which will provide the following features:-

1. Researcher can plan to survey a location by loading its map.

2. Use of android APIs to measure network signal strengths for different network providers like AT&T, Verizon, Tracfone, WI-Fi across multiple locations.

3. Calculate Page load time for Top 100 websites using different network providers across locations. (http://www.alexa.com/topsites/countries/US)

4. Analyse and prepare report to estimate the PLTs behaviour for different network signal providers along with their strengths across locations.

Development & Tool used: This will involve development of location agnostic generic android app with network scanning flexibility to dump different network parameters, especially Signal strength measure its performance over different web page load time across locations. We might also need to use some mobile tool to measure PLT/ or develop customized integrated app to measure PLTs with above said signal strengths.
Metric: Estimation of Page load time for various websites over multiple network strengths across locations. Network Signal Strength Vs PLTs, given location. Network Providers Vs PLTs, given location and a web page.

Example: We measure PLTs for Top 100 sites (http://www.alexa.com/topsites/countries/US) using different Network Signal strength(AT&T, Verizon and Tracfone, Wi-Fi) at different locations, Stony Brook University.
